package com.backend.users.graph;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node("User")
@Data
@NoArgsConstructor
public class UserNode {
    @Id
    private Long id;

    private String email;

    @Relationship(type = "FRIENDS_WITH", direction = OUTGOING)
    private Set<UserNode> friends = new HashSet<>();

    @Relationship(type = "FOLLOWS", direction = OUTGOING)
    private List<FollowsRelationship> following = new ArrayList<>();

    @Relationship(type = "BLOCKS", direction = OUTGOING)
    private List<BlocksRelationship> blocked = new ArrayList<>();

    @Relationship(type = "IN_LIST", direction = OUTGOING)
    private List<FriendListRelationship> friendLists = new ArrayList<>();

    public UserNode(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}
