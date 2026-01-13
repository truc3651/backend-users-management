CREATE SEQUENCE users.q_password_reset_tokens_id INCREMENT BY 10 MINVALUE 1 NO MAXVALUE START WITH 1 CACHE 10 NO CYCLE;

CREATE TABLE users.t_password_reset_tokens (
    id BIGINT PRIMARY KEY DEFAULT nextval('users.q_password_reset_tokens_id'),
    token VARCHAR(255) UNIQUE,
    user_id BIGINT,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used BOOLEAN,
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users.t_users(id) ON DELETE CASCADE
);

CREATE INDEX t_password_reset_tokens_idx_token ON users.t_password_reset_tokens USING HASH(token);
CREATE INDEX t_password_reset_tokens_idx_user_id ON users.t_password_reset_tokens USING HASH(user_id);
-- CREATE INDEX idx_password_reset_tokens_expires_at ON t_password_reset_tokens(expires_at);
