package com.thoughtworks.wechat_application.jdbi;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.sql.Timestamp;

public interface WeChatEventLogDAO {
    @SqlUpdate("INSERT INTO WeChatEventLog (MemberId, EventType, EventName, EventValue, HappenedTime)" +
            "   VALUES (:memberId, :eventType, :eventName, :eventValue, :happenedTime)")
    @GetGeneratedKeys
    long insertEventLog(@Bind("memberId") final long memberId,
                        @Bind("eventType") final String eventType,
                        @Bind("eventName") final String eventName,
                        @Bind("eventValue") final String eventValue,
                        @Bind("happenedTime") final Timestamp happenedTime);

    @SqlUpdate("INSERT INTO WeChatEventLog (MemberId, EventType, EventName, HappenedTime)" +
            "   VALUES (:memberId, :eventType, :eventName, :happenedTime)")
    @GetGeneratedKeys
    long insertEventLog(@Bind("memberId") final long memberId,
                        @Bind("eventType") final String eventType,
                        @Bind("eventName") final String eventName,
                        @Bind("happenedTime") final Timestamp happenedTime);
}
