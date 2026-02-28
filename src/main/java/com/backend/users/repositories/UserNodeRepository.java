package com.backend.users.repositories;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.users.graph.UserNode;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserNodeRepository extends ReactiveNeo4jRepository<UserNode, Long> {
  @Query("MATCH (u:User {id: $userId})-[:FRIENDS_WITH]->(fr:User) RETURN fr")
  Flux<UserNode> findFriendsByUserId(@Param("userId") Long userId);

  @Query(
      """
        MATCH (u:User {id: $userId})-[:FRIENDS_WITH*2..2]->(fof:User)
        WHERE NOT (u)-[:FRIENDS_WITH]->(fof) AND u <> fof
        RETURN DISTINCT fof
        LIMIT $limit
    """)
  Flux<UserNode> findFriendsOfFriends(@Param("userId") Long userId, @Param("limit") int limit);

  @Query(
      "MATCH (u:User {id: $userId})-[:FOLLOWS]->(followed:User) RETURN followed SKIP $skip LIMIT"
          + " $limit")
  Flux<UserNode> findFollowingPaginated(
      @Param("userId") Long userId, @Param("skip") long skip, @Param("limit") int limit);

  @Query("MATCH (u:User {id: $userId})-[:FOLLOWS]->(followed:User) RETURN count(followed)")
  Mono<Long> countFollowing(@Param("userId") Long userId);

  @Query(
      "MATCH (follower:User)-[:FOLLOWS]->(u:User {id: $userId}) RETURN follower SKIP $skip LIMIT"
          + " $limit")
  Flux<UserNode> findFollowersPaginated(
      @Param("userId") Long userId, @Param("skip") long skip, @Param("limit") int limit);

  @Query("MATCH (follower:User)-[:FOLLOWS]->(u:User {id: $userId}) RETURN count(follower)")
  Mono<Long> countFollowers(@Param("userId") Long userId);

  @Query(
      "MATCH (u:User {id: $userId})-[:BLOCKS]->(blocked:User) RETURN blocked SKIP $skip LIMIT"
          + " $limit")
  Flux<UserNode> findBlockedUsersPaginated(
      @Param("userId") Long userId, @Param("skip") long skip, @Param("limit") int limit);

  @Query("MATCH (u:User {id: $userId})-[:BLOCKS]->(blocked:User) RETURN count(blocked)")
  Mono<Long> countBlockedUsers(@Param("userId") Long userId);
}
