package com.thoughtworks.wechat_application.jdbi;

import com.thoughtworks.wechat_application.jdbi.core.Label;
import com.thoughtworks.wechat_application.jdbi.mapper.LabelMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.sql.Timestamp;
import java.util.List;

@RegisterMapper(LabelMapper.class)
public interface LabelDAO {
    @SqlUpdate("INSERT INTO Label (Title, CreatedTime) VALUES (:title, :createdTime)")
    @GetGeneratedKeys
    long createLabel(@Bind("title") final String title,
                     @Bind("createdTime") final Timestamp createdTime);

    @SqlQuery("SELECT * FROM Label")
    List<Label> getAllLabel();

    @SqlUpdate("DELETE FROM Label WHERE Id = :id")
    void deleteLabel(@Bind("id") final long id);

    @SqlQuery("SELECT l.* FROM TextMessageLabelRelation AS r" +
            "  JOIN Label AS l" +
            "  ON l.Id = r.LabelId" +
            "  WHERE r.TextMessageId = :textMessageId")
    List<Label> getTextMessageLabels(@Bind("textMessageId") final long textMessageId);
}
