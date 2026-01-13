package com.example.graph;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.OffsetDateTime;

@RelationshipProperties
@Data
public class FollowsRelationship {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private UserNode followedUser;

    private OffsetDateTime createdAt;

    public FollowsRelationship(UserNode followedUser) {
        this.followedUser = followedUser;
        this.createdAt = OffsetDateTime.now();
    }
}
