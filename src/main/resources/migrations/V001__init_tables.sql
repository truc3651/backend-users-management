CREATE SEQUENCE users.q_users_id INCREMENT BY 10 MINVALUE 1 NO MAXVALUE START WITH 1 CACHE 10 NO CYCLE;
CREATE TABLE users.t_users (
    id BIGINT PRIMARY KEY DEFAULT nextval('users.q_users_id'),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    created_at TIMESTAMP with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP with time zone
);

-----------

CREATE TABLE users.t_refresh_tokens (
    token VARCHAR(255),
    user_id BIGINT,
    expires_at TIMESTAMP with time zone,
    created_at TIMESTAMP with time zone DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX t_refresh_tokens_idx_refresh_token ON users.t_refresh_tokens USING HASH(token);

-----------

CREATE SEQUENCE users.q_friend_requests_id INCREMENT BY 10 MINVALUE 1 NO MAXVALUE START WITH 1 CACHE 10 NO CYCLE;
CREATE TABLE users.t_friend_requests (
    id BIGINT DEFAULT nextval('users.q_friend_requests_id') PRIMARY KEY,
    requester_id BIGINT,
    addressee_id BIGINT,
    status VARCHAR(50),
    created_at TIMESTAMP with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP with time zone
);

CREATE INDEX t_friend_requests_idx_requester_id ON users.t_friend_requests(requester_id);
CREATE INDEX t_friend_requests_idx_addressee_id ON users.t_friend_requests(addressee_id);

-----------

CREATE TABLE users.t_password_reset_tokens (
   token VARCHAR(255) PRIMARY KEY,
   user_id BIGINT,
   expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
   created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX t_password_reset_tokens_idx_token ON users.t_password_reset_tokens USING HASH(token);
