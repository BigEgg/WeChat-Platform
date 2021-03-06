package com.thoughtworks.wechat_application.logic;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.thoughtworks.wechat_application.configs.OAuthConfiguration;
import com.thoughtworks.wechat_application.jdbi.core.AuthenticateRole;
import com.thoughtworks.wechat_application.jdbi.core.OAuthClient;
import com.thoughtworks.wechat_application.models.oauth.OAuthInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OAuthProviderTest {
    @Mock
    private OAuthConfiguration configuration;
    private OAuthProvider oAuthProvider;
    private Injector injector;

    @Before
    public void setUp() throws Exception {
        injector = Guice.createInjector(binder -> {
            binder.bind(OAuthConfiguration.class).toInstance(configuration);
        });

        oAuthProvider = injector.getInstance(OAuthProvider.class);
    }

    @Test
    public void testInject_Singleton() throws Exception {
        final OAuthProvider anotherProvider = injector.getInstance(OAuthProvider.class);
        assertThat(anotherProvider, equalTo(oAuthProvider));
    }

    @Test
    public void testNewOAuth() throws Exception {
        when(configuration.getoAuthAccessTokenExpireSeconds()).thenReturn(10);
        when(configuration.getoAuthRefreshTokenExpireSeconds()).thenReturn(10);

        final OAuthInfo oAuthInfo = oAuthProvider.newOAuth(createAdmin());

        assertThat(oAuthInfo.getAccessToken().isPresent(), equalTo(true));
        assertThat(oAuthInfo.getAccessToken().get(), any(String.class));
        assertThat(oAuthInfo.getRefreshToken().isPresent(), equalTo(true));
        assertThat(oAuthInfo.getRefreshToken().get(), any(String.class));
    }

    @Test
    public void testNewOAuth_ExistAdmin() throws Exception {
        when(configuration.getoAuthAccessTokenExpireSeconds()).thenReturn(10);
        when(configuration.getoAuthRefreshTokenExpireSeconds()).thenReturn(10);

        final OAuthClient admin = createAdmin();
        final OAuthInfo oAuthInfo = oAuthProvider.newOAuth(admin);
        final OAuthInfo oAuthInfo2 = oAuthProvider.newOAuth(admin);

        assertThat(oAuthInfo2.getAccessToken().isPresent(), equalTo(true));
        assertThat(oAuthInfo2.getAccessToken().get(), not(equalTo(oAuthInfo.getAccessToken().get())));
        assertThat(oAuthInfo2.getRefreshToken().isPresent(), equalTo(true));
        assertThat(oAuthInfo2.getRefreshToken().get(), not(equalTo(oAuthInfo.getRefreshToken().get())));
        assertThat(oAuthInfo2.getClient(), equalTo(oAuthInfo.getClient()));
    }

    @Test
    public void testRefreshAccessToken() throws Exception {
        when(configuration.getoAuthAccessTokenExpireSeconds()).thenReturn(1, 10);
        when(configuration.getoAuthRefreshTokenExpireSeconds()).thenReturn(10);

        final OAuthInfo oAuthInfo = oAuthProvider.newOAuth(createAdmin());
        final String accessToken = oAuthInfo.getAccessToken().get();
        Thread.sleep(1000);

        final Optional<OAuthInfo> newOAuthInfoOpt = oAuthProvider.refreshAccessToken(accessToken, oAuthInfo.getRefreshToken().get());
        assertThat(newOAuthInfoOpt.isPresent(), equalTo(true));

        final OAuthInfo newOAuthInfo = newOAuthInfoOpt.get();
        assertThat(newOAuthInfo.getAccessToken().isPresent(), equalTo(true));
        assertThat(newOAuthInfo.getAccessToken().get(), any(String.class));
        assertThat(newOAuthInfo.getRefreshToken().isPresent(), equalTo(true));
        assertThat(newOAuthInfo.getRefreshToken().get(), any(String.class));
    }

    @Test
    public void testRefreshAccessToken_NoMatchAccessToken() throws Exception {
        when(configuration.getoAuthAccessTokenExpireSeconds()).thenReturn(1);
        when(configuration.getoAuthRefreshTokenExpireSeconds()).thenReturn(10);

        final OAuthInfo oAuthInfo = oAuthProvider.newOAuth(createAdmin());
        Thread.sleep(1000);

        final Optional<OAuthInfo> newOAuthInfoOpt = oAuthProvider.refreshAccessToken("wrongAccessToken", oAuthInfo.getRefreshToken().get());
        assertThat(newOAuthInfoOpt.isPresent(), equalTo(false));
    }

    @Test
    public void testRefreshAccessToken_RefreshTokenExpired() throws Exception {
        when(configuration.getoAuthAccessTokenExpireSeconds()).thenReturn(10);
        when(configuration.getoAuthRefreshTokenExpireSeconds()).thenReturn(1);

        final OAuthInfo oAuthInfo = oAuthProvider.newOAuth(createAdmin());
        final String accessToken = oAuthInfo.getAccessToken().get();
        final String refreshToken = oAuthInfo.getRefreshToken().get();
        Thread.sleep(1000);

        final Optional<OAuthInfo> newOAuthInfoOpt = oAuthProvider.refreshAccessToken(accessToken, refreshToken);
        assertThat(newOAuthInfoOpt.isPresent(), equalTo(false));
    }

    @Test
    public void testGetOAuthClient() throws Exception {
        when(configuration.getoAuthAccessTokenExpireSeconds()).thenReturn(10);
        when(configuration.getoAuthRefreshTokenExpireSeconds()).thenReturn(10);

        final OAuthClient admin = createAdmin();
        final OAuthInfo oAuthInfo = oAuthProvider.newOAuth(admin);

        final Optional<OAuthClient> newOAuthClient = oAuthProvider.getOAuthClient(oAuthInfo.getAccessToken().get());
        assertThat(newOAuthClient.isPresent(), equalTo(true));
        assertThat(newOAuthClient.get().getClientId(), equalTo("clientId"));
    }

    @Test
    public void testRemoveOAuthInfo() throws Exception {
        when(configuration.getoAuthAccessTokenExpireSeconds()).thenReturn(10);
        when(configuration.getoAuthRefreshTokenExpireSeconds()).thenReturn(10);

        final OAuthClient admin = createAdmin();
        final OAuthInfo oAuthInfo = oAuthProvider.newOAuth(admin);

        Optional<OAuthClient> newOAuthClient = oAuthProvider.removeOAuthInfo(oAuthInfo.getAccessToken().get());
        assertThat(newOAuthClient.isPresent(), equalTo(true));
        assertThat(newOAuthClient.get().getClientId(), equalTo("clientId"));

        newOAuthClient = oAuthProvider.getOAuthClient(oAuthInfo.getAccessToken().get());
        assertThat(newOAuthClient.isPresent(), equalTo(false));
    }

    private OAuthClient createAdmin() {
        return new OAuthClient(1L, "clientId", "hashedClientSecret", AuthenticateRole.ADMIN, Optional.<Long>empty());
    }
}