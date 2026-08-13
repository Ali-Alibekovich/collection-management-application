package io.github.alialibekovich.collection.server.db;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHashingTest {

    @Test
    void hashVerifiesAgainstOriginalPassword() {
        String hash = UsersRepository.hashPassword("s3cret");

        assertTrue(BCrypt.checkpw("s3cret", hash));
        assertFalse(BCrypt.checkpw("wrong", hash));
    }

    @Test
    void samePasswordProducesDifferentHashes() {
        // bcrypt embeds a random salt, so equal passwords must not collide
        assertNotEquals(UsersRepository.hashPassword("s3cret"), UsersRepository.hashPassword("s3cret"));
    }
}
