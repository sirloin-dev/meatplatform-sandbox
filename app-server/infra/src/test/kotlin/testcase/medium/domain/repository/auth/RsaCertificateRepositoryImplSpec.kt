/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package testcase.medium.domain.repository.auth

import com.sirloin.jvmlib.time.truncateToSeconds
import io.kotest.matchers.shouldBe
import net.meatplatform.sandbox.domain.model.auth.RsaCertificate
import net.meatplatform.sandbox.domain.repository.auth.RsaCertificateRepository
import net.meatplatform.sandbox.domain.repository.auth.RsaCertificateRepositoryImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import test.domain.usecase.auth.random
import testcase.medium.MediumTestBase
import java.time.Instant

/**
 * @since 2022-12-29
 */
@ContextConfiguration(classes = [RsaCertificateRepositoryImpl::class])
class RsaCertificateRepositoryImplSpec : MediumTestBase() {
    @Autowired
    private lateinit var instance: RsaCertificateRepository

    private lateinit var sut: RsaCertificateRepositoryImpl

    @BeforeEach
    fun setup() {
        // 변환 실패시 테스트 전체 실패
        this.sut = instance as RsaCertificateRepositoryImpl
    }

    @AfterEach
    fun teardown() {
        sut.lruCache.clear()
        sut.cachedLatestActive = null
    }

    @DisplayName("issueRandom 실행시 파라미터로 주어진 시간을 기준으로 RSA 인증서를 생성한다.")
    @Test
    fun issueRandomWorksWell() {
        // given:
        val now = Instant.now().truncateToSeconds()

        // then:
        val certificate = sut.issueRandom(now)

        // expect:
        certificate.issuedAt shouldBe now
    }

    @DisplayName("저장한 인증서를 ID 로 다시 조회할 수 있다.")
    @Test
    fun findSavedCertificateById() {
        // given:
        val cert = RsaCertificate.random()

        // when:
        val savedCert = sut.save(cert)

        // then:
        val foundCert = sut.findById(cert.id)

        // expect:
        foundCert shouldBe savedCert
    }

    @DisplayName("isEnabled = false 로 설정한 인증서는 조회할 수 없다.")
    @Test
    fun disabledCertificateNotBeFound() {
        // given:
        val disabledCert = sut.save(RsaCertificate.random(isEnabled = false))

        // then:
        val foundCert = sut.findById(disabledCert.id)

        // expect:
        foundCert shouldBe null
    }

    @DisplayName("Active latest 를 검색할 때:")
    @Nested
    inner class WhileQueryingActiveLatestCertificate {
        @DisplayName("현재 시점이 activeUntil 이후라면 키를 검색할 수 없다")
        @Test
        fun noCertIsFoundIfMostRecentActiveLatestIsAlreadyPast() {
            // given:
            sut.save(RsaCertificate.random(isEnabled = false))

            // then:
            val foundCert = sut.findCurrentlyActive()

            // expect:
            foundCert shouldBe null
        }

        @DisplayName("activeUntil 이 유효한 키가 여럿 있으면, 가장 최근에 생성한 키 한개만 검색한다")
        @Test
        fun theMostActiveLatestIsChosen() {
            // given:
            val (oneHourAgo, yesterday) = Instant.now().let {
                it.minusSeconds(60 * 60) to it.minusSeconds(24 * 60 * 60)
            }
            val farFuture = Instant.parse("3000-01-01T00:00:00Z")

            // and:
            val certs = listOf(
                RsaCertificate.random(issuedAt = yesterday, activeUntil = farFuture),
                RsaCertificate.random(issuedAt = oneHourAgo, activeUntil = farFuture)
            ).onEach { sut.save(it) }
            val expected = certs[1]

            // then:
            val foundCert = sut.findCurrentlyActive()

            // expect:
            foundCert shouldBe expected
        }
    }
}
