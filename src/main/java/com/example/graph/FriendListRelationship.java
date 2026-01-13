package com.example.graph;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.OffsetDateTime;

@RelationshipProperties
@Data
public class FriendListRelationship {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private UserNode friend;

    private String name;
    private OffsetDateTime createdAt;

    public FriendListRelationship(UserNode friend, String name) {
        this.friend = friend;
        this.name = name;
        this.createdAt = OffsetDateTime.now();
    }
}
