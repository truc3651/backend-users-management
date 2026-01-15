package com.backend.users.graph;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.OffsetDateTime;

@RelationshipProperties
@Data
public class BlocksRelationship {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private UserNode blockedUser;

    private OffsetDateTime createdAt;
    private String reason;

    public BlocksRelationship(UserNode blockedUser, String reason) {
        this.blockedUser = blockedUser;
        this.reason = reason;
        this.createdAt = OffsetDateTime.now();
    }
}
