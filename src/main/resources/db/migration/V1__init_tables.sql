CREATE SCHEMA IF NOT EXISTS users;

CREATE SEQUENCE users.q_users_id INCREMENT BY 10 MINVALUE 1 NO MAXVALUE START WITH 1 CACHE 10 NO CYCLE;
CREATE TABLE users.t_users (
    id BIGINT DEFAULT nextval('users.q_users_id') PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50),
    created_at TIMESTAMP with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP with time zone
);
CREATE INDEX t_users_idx_email ON users.t_users USING HASH (email);

-----------

CREATE SEQUENCE users.q_refresh_tokens_id INCREMENT BY 10 MINVALUE 1 NO MAXVALUE START WITH 1 CACHE 10 NO CYCLE;
CREATE TABLE users.t_refresh_tokens (
    id BIGINT DEFAULT nextval('users.q_refresh_tokens_id') PRIMARY KEY,
    token VARCHAR(255) UNIQUE,
    user_id BIGINT,
    expires_at TIMESTAMP with time zone,
    created_at TIMESTAMP with time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users.t_users(id) ON DELETE CASCADE
);
CREATE INDEX t_refresh_tokens_idx_refresh_token ON users.t_refresh_tokens USING HASH(token);
CREATE INDEX t_refresh_tokens_idx_user_id ON users.t_refresh_tokens USING HASH(user_id);
CREATE INDEX t_refresh_tokens_idx_expires_at ON users.t_refresh_tokens(expires_at);

-----------

CREATE SEQUENCE users.q_friend_requests_id INCREMENT BY 10 MINVALUE 1 NO MAXVALUE START WITH 1 CACHE 10 NO CYCLE;
CREATE TABLE users.t_friend_requests (
    id BIGINT DEFAULT nextval('users.q_friend_requests_id') PRIMARY KEY,
    requester_id BIGINT,
    addressee_id BIGINT,
    status VARCHAR(50),
    created_at TIMESTAMP with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP with time zone,
    CONSTRAINT fk_friend_request_requester FOREIGN KEY (requester_id) REFERENCES users.t_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_request_addressee FOREIGN KEY (addressee_id) REFERENCES users.t_users(id) ON DELETE CASCADE,
    CONSTRAINT unique_friend_request UNIQUE (requester_id, addressee_id)
);

CREATE INDEX t_friend_requests_idx_requester_id ON users.t_friend_requests(requester_id);
CREATE INDEX t_friend_requests_idx_addressee_id ON users.t_friend_requests(addressee_id);
