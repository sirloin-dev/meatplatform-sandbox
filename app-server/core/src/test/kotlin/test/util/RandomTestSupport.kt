/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package test.util

import com.github.javafaker.Faker
import com.sirloin.jvmlib.util.SemanticVersion
import test.com.sirloin.util.text.randomFillChars
import kotlin.random.Random

fun randomAlphanumeric(min: Int = 1, max: Int = 32): String =
    Faker().letterify(randomFillChars('?', min, max))

fun randomSemanticVersion(
    major: Int = Random.nextInt(100),
    minor: Int = Random.nextInt(100),
    patch: Int = Random.nextInt(100),
    preRelease: String = randomAlphanumeric(0, 8),
    build: String = "1",
) = SemanticVersion(major, minor, patch, preRelease, build)
