package com.thoughtworks.wechat_application.resources.admin.basicSettings;

import com.thoughtworks.wechat_application.api.admin.wechat.DeveloperInfoResponse;
import com.thoughtworks.wechat_application.api.admin.wechat.NewDeveloperInfoRequest;
import com.thoughtworks.wechat_application.api.admin.wechat.ServerInfoResponse;
import com.thoughtworks.wechat_application.api.admin.wechat.WeChatConnectionStatusResponse;
import com.thoughtworks.wechat_application.jdbi.core.AuthenticateRole;
import com.thoughtworks.wechat_application.jdbi.core.OAuthClient;
import com.thoughtworks.wechat_application.logic.OAuthProvider;
import com.thoughtworks.wechat_application.resources.ResourceTestBase;
import com.thoughtworks.wechat_application.services.admin.AdminResourceService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class WeChatBasicSettingsResourceTest extends ResourceTestBase {
    private final static AdminResourceService adminResourceService = mock(AdminResourceService.class);
    private final static OAuthProvider oAuthProvider = mock(OAuthProvider.class);

    @ClassRule
    public final static ResourceTestRule resource = ResourceTestRule.builder()
            .addResource(new WeChatBasicSettingsResource(adminResourceService, oAuthProvider))
            .build();

    private static OAuthClient createAdmin() {
        return new OAuthClient(1L, "clientId", "hashedClientSecret", AuthenticateRole.ADMIN, Optional.<Long>empty());
    }

    private static OAuthClient createVendor() {
        return new OAuthClient(1L, "clientId", "hashedClientSecret", AuthenticateRole.VENDOR, Optional.<Long>empty());
    }

    public static class when_try_to_got_weChat_connection_status {
        @After
        public void tearDown() throws Exception {
            reset(adminResourceService);
            reset(oAuthProvider);
        }

        @Test
        public void return_server_info_if_access_token_valid() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.of(createAdmin()));
            when(adminResourceService.weChat()).thenReturn(mock(AdminResourceService.WeChatService.class));
            when(adminResourceService.weChat().getConnectionStatus()).thenReturn(true);
            when(adminResourceService.weChat().getAPIStatus()).thenReturn(false);

            final Response response = resource.client().target("/api/admin/wechat/status").queryParam("access_token", "accessToken").request().get();
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

            final WeChatConnectionStatusResponse status = getResponseEntity(response, WeChatConnectionStatusResponse.class);
            assertThat(status).isNotNull();
            assertThat(status.getWeChatConnectedStatus()).isTrue();
            assertThat(status.getWeChatAPIStatus()).isFalse();

            verify(oAuthProvider).getOAuthClient(eq("accessToken"));
            verify(adminResourceService.weChat()).getConnectionStatus();
            verify(adminResourceService.weChat()).getAPIStatus();
        }

        @Test
        public void throw_forbidden_if_access_token_failed() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.<OAuthClient>empty());

            final Response response = resource.client().target("/api/admin/wechat/status").queryParam("access_token", "accessToken").request().get();
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void throw_forbidden_if_access_token_not_admin() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.of(createVendor()));

            final Response response = resource.client().target("/api/admin/wechat/status").queryParam("access_token", "accessToken").request().get();
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static class when_try_to_get_server_info {
        @After
        public void tearDown() throws Exception {
            reset(adminResourceService);
            reset(oAuthProvider);
        }

        @Test
        public void return_server_info_if_access_token_valid() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.of(createAdmin()));
            when(adminResourceService.weChat()).thenReturn(mock(AdminResourceService.WeChatService.class));
            when(adminResourceService.weChat().getAppToken()).thenReturn("abcdefghijklmn");

            final Response response = resource.client().target("/api/admin/wechat/server").queryParam("access_token", "accessToken").request().get();
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

            final ServerInfoResponse serverInfo = getResponseEntity(response, ServerInfoResponse.class);
            assertThat(serverInfo).isNotNull();
            assertThat(serverInfo.getEntryPoint()).isEqualTo("/wechat");
            assertThat(serverInfo.getAppToken()).isEqualTo("abcdefghijklmn");

            verify(oAuthProvider).getOAuthClient(eq("accessToken"));
            verify(adminResourceService.weChat()).getAppToken();
        }

        @Test
        public void throw_forbidden_if_access_token_failed() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.<OAuthClient>empty());

            final Response response = resource.client().target("/api/admin/wechat/server").queryParam("access_token", "accessToken").request().get();
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void throw_forbidden_if_access_token_not_admin() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.of(createVendor()));

            final Response response = resource.client().target("/api/admin/wechat/server").queryParam("access_token", "accessToken").request().get();
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static class when_try_to_get_developer_info {
        @After
        public void tearDown() throws Exception {
            reset(adminResourceService);
            reset(oAuthProvider);
        }

        @Test
        public void return_server_info_if_access_token_valid() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.of(createAdmin()));
            when(adminResourceService.weChat()).thenReturn(mock(AdminResourceService.WeChatService.class));
            when(adminResourceService.weChat().getAppId()).thenReturn("app_id");
            when(adminResourceService.weChat().getAppSecret()).thenReturn("app_secret");

            final Response response = resource.client().target("/api/admin/wechat/developer").queryParam("access_token", "accessToken").request().get();
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

            final DeveloperInfoResponse developerInfo = getResponseEntity(response, DeveloperInfoResponse.class);
            assertThat(developerInfo).isNotNull();
            assertThat(developerInfo.getAppId()).isEqualTo("app_id");
            assertThat(developerInfo.getAppSecret()).isEqualTo("app_secret");

            verify(oAuthProvider).getOAuthClient(eq("accessToken"));
            verify(adminResourceService.weChat()).getAppId();
            verify(adminResourceService.weChat()).getAppSecret();
        }

        @Test
        public void throw_forbidden_if_access_token_failed() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.<OAuthClient>empty());

            final Response response = resource.client().target("/api/admin/wechat/developer").queryParam("access_token", "accessToken").request().get();
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void throw_forbidden_if_access_token_not_admin() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.of(createVendor()));

            final Response response = resource.client().target("/api/admin/wechat/developer").queryParam("access_token", "accessToken").request().get();
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static class when_try_to_set_developer_info {
        @After
        public void tearDown() throws Exception {
            reset(adminResourceService);
            reset(oAuthProvider);
        }

        @Test
        public void return_server_info_if_access_token_valid() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.of(createAdmin()));
            when(adminResourceService.weChat()).thenReturn(mock(AdminResourceService.WeChatService.class));
            when(adminResourceService.weChat().getAppId()).thenReturn("app_id");
            when(adminResourceService.weChat().getAppSecret()).thenReturn("app_secret");

            final NewDeveloperInfoRequest request = deserializeFixture("fixtures/admin/wechat/NewDeveloperInfoRequest.json", NewDeveloperInfoRequest.class);
            final Response response = resource.client().target("/api/admin/wechat/developer").queryParam("access_token", "accessToken").request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

            final DeveloperInfoResponse developerInfo = getResponseEntity(response, DeveloperInfoResponse.class);
            assertThat(developerInfo).isNotNull();
            assertThat(developerInfo.getAppId()).isEqualTo("app_id");
            assertThat(developerInfo.getAppSecret()).isEqualTo("app_secret");

            verify(oAuthProvider).getOAuthClient(eq("accessToken"));
            verify(adminResourceService.weChat()).setAppId(eq("app_id"));
            verify(adminResourceService.weChat()).setAppSecret(eq("app_secret"));
        }

        @Test
        public void throw_internal_server_error_if_access_token_failed() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.<OAuthClient>empty());

            final NewDeveloperInfoRequest request = deserializeFixture("fixtures/admin/wechat/NewDeveloperInfoRequest.json", NewDeveloperInfoRequest.class);
            final Response response = resource.client().target("/api/admin/wechat/developer").queryParam("access_token", "accessToken").request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void throw_internal_server_error_if_access_token_not_admin() throws Exception {
            when(oAuthProvider.getOAuthClient("accessToken")).thenReturn(Optional.of(createVendor()));

            final NewDeveloperInfoRequest request = deserializeFixture("fixtures/admin/wechat/NewDeveloperInfoRequest.json", NewDeveloperInfoRequest.class);
            final Response response = resource.client().target("/api/admin/wechat/developer").queryParam("access_token", "accessToken").request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
            assertThat(response.getStatusInfo()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}