package com.example.repositories;

import com.example.graph.UserNode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {
    @Query("MATCH (u:User {id: $userId})-[:FRIENDS_WITH]->(fr:User) RETURN fr")
    List<UserNode> findFriendsByUserId(@Param("userId") Long userId);

    @Query("""
        MATCH (u:User {id: $userId})-[:FRIENDS_WITH*2..2]->(fof:User)
        WHERE NOT (u)-[:FRIENDS_WITH]->(fof) AND u <> fof
        RETURN DISTINCT fof
        LIMIT $limit
    """)
    List<UserNode> findFriendsOfFriends(@Param("userId") Long userId, @Param("limit") int limit);

    @Query("""
        MATCH (u:User {id: $userId})-[r:IN_LIST]->(friend:User {id: $friendId})
        RETURN DISTINCT r.listName
    """)
    List<String> findListsContainingFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("""
        MATCH (u:User {id: $userId}), (fr:User {id: $friendId})
        CREATE (u)-[:IN_LIST {listName: $listName, createdAt: datetime()}]->(fr)
    """)
    void addToFriendList(@Param("userId") Long user, @Param("friendId") Long friendId, @Param("listName") String listName);

    @Query("""
        MATCH (u:User {id: $userId})-[r:IN_LIST {listName: $listName}]->(fr:User {id: $friendId})
        DELETE r
    """)
    void removeFromFriendList(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("listName") String listName);

    @Query("""
        MATCH (u:User {id: $userId})-[r:IN_LIST {listName: $listName}]->(fr:User)
        RETURN fr
    """)
    List<UserNode> findFriendsByList(@Param("userId") Long userId, @Param("listName") String listName);

    @Query("""
        MATCH (u:User {id: $userId})-[r:IN_LIST]->(fr:User)
        RETURN DISTINCT r.listName
        ORDER BY r.listName
    """)
    List<String> findAllFriendListNames(@Param("userId") Long userId);

    @Query("""
        MATCH (followed:User {id: $followedId})-[:BLOCKS]->(follower:User {id: $followerId})
        RETURN COUNT(*) > 0
        UNION
        MATCH (follower:User {id: $followerId})-[:BLOCKS]->(followed:User {id: $followedId})
        RETURN COUNT(*) > 0
    """)
    boolean areBlocking(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

    @Query("""
        MATCH (follower:User {id: $followerId}), (followed:User {id: $followedId})
        MERGE (follower)-[:FOLLOWS {createdAt: datetime()}]->(followed)
    """)
    void upsertFollow(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

    @Query("""
        MATCH (follower:User {id: $followerId})
        OPTIONAL MATCH (follower)-[r:FOLLOWS]->(followed:User {id: $followedId})
        DELETE r
    """)
    void deleteFollow(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

    @Query(value = "MATCH (u:User {id: $userId})-[:FOLLOWS]->(followed:User) RETURN followed",
           countQuery = "MATCH (u:User {id: $userId})-[:FOLLOWS]->(followed:User) RETURN count(followed)")
    Page<UserNode> findFollowingPaginated(
            @Param("userId") Long userId,
            Pageable pageable);

    @Query(value = "MATCH (follower:User)-[:FOLLOWS]->(u:User {id: $userId}) RETURN follower",
           countQuery = "MATCH (follower:User)-[:FOLLOWS]->(u:User {id: $userId}) RETURN count(follower)")
    Page<UserNode> findFollowersPaginated(
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("""
        MATCH (blocker:User {id: $blockerId}), (blocked:User {id: $blockedId})
        MERGE (blocker)-[:BLOCKS {createdAt: datetime(), reason: $reason}]->(blocked)
        WITH blocker, blocked
        OPTIONAL MATCH (blocker)-[rFW:FRIENDS_WITH]-(blocked) DELETE rFW
        WITH blocker, blocked
        OPTIONAL MATCH (blocker)-[rF1:FOLLOWS]-(blocked) DELETE rF1
        WITH blocker, blocked
        OPTIONAL MATCH (blocked)-[rF2:FOLLOWS]->(blocker) DELETE rF2
    """)
    void upsertBlock(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId, @Param("reason") String reason);

    @Query("""
        MATCH (blocker:User {id: $blockerId})
        OPTIONAL MATCH (blocker)-[r:BLOCKS]->(blocked:User {id: $blockedId})
        DELETE r
    """)
    void deleteBlock(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);

    @Query(value = "MATCH (u:User {id: $userId})-[:BLOCKS]->(blocked:User) RETURN blocked",
           countQuery = "MATCH (u:User {id: $userId})-[:BLOCKS]->(blocked:User) RETURN count(blocked)")
    Page<UserNode> findBlockedUsersPaginated(
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("""
        MATCH (u:User {id: $userId}), (fr:User {id: $friendId})
        CREATE (u)-[:FRIENDS_WITH]->(fr), (fr)-[:FRIENDS_WITH]->(u)
    """)
    void createFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("MATCH (u:User {id: $userId})-[r:FRIENDS_WITH]-(fr:User {id: $friendId}) DELETE fr")
    void deleteFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
