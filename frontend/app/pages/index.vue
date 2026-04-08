<script setup lang="ts">
import type { Travel } from '~/app/types/api'

const api = useApi()
const { data: travels, error } = await useAsyncData('home-travels', () => api.getTravels())

const featuredTravels = computed<Travel[]>(() => (travels.value || []).slice(0, 2))
</script>

<template>
  <div class="home-shell">
    <section class="home-hero">
      <div class="home-hero__main">
        <span class="home-kicker">여행 포토북을 더 간단하게</span>
        <h1 class="home-title">
          여행의 순간을
          <br>
          깔끔하게 한 권으로
        </h1>
        <p class="home-copy">
          사진 정리부터 프리뷰, 주문까지 한 번에 이어지는
          여행 포토북 작업 흐름을 제공합니다.
        </p>

        <div class="actions">
          <NuxtLink class="button" to="/travels/new">새 여행 시작</NuxtLink>
          <NuxtLink class="ghost-button" to="/travels">프로젝트 보기</NuxtLink>
        </div>

        <div class="home-process">
          <article class="home-process__item">
            <strong>사진 업로드</strong>
            <span>사진과 메모를 먼저 정리합니다.</span>
          </article>
          <article class="home-process__item">
            <strong>프리뷰 제작</strong>
            <span>템플릿을 고르고 구성을 확인합니다.</span>
          </article>
          <article class="home-process__item">
            <strong>주문 연결</strong>
            <span>견적 확인 후 주문까지 이어집니다.</span>
          </article>
        </div>
      </div>

      <aside class="home-hero__side">
        <div class="home-photo-card">
          <img
            src="https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=80"
            alt="포토북 무드 이미지"
          >
          <div class="home-photo-card__overlay">
            <span class="home-chip home-chip--blue">PhotoBook Mood</span>
            <strong>여행의 장면을 더 오래 남겨보세요</strong>
            <p>정리된 사진과 기록이 한 권의 포토북으로 이어집니다.</p>
          </div>
        </div>
      </aside>
    </section>

    <section class="home-benefit-grid">
      <article class="home-info-card">
        <span class="home-chip">한눈에 정리</span>
        <h2>복잡하지 않은 화면</h2>
        <p>핵심 정보만 먼저 보여줘서 제출용 시연에서도 흐름을 바로 설명할 수 있습니다.</p>
      </article>

      <article class="home-info-card">
        <span class="home-chip">빠른 확인</span>
        <h2>프리뷰와 상태 확인</h2>
        <p>초안, 제작 완료, 주문 상태를 구분해서 지금 어디까지 진행됐는지 쉽게 파악할 수 있습니다.</p>
      </article>

      <article class="home-info-card">
        <span class="home-chip">실제 연결</span>
        <h2>SweetBook 연동 기반</h2>
        <p>포토북 생성과 주문 흐름이 실제 API와 연결되어 있어 과제 시연용으로도 설득력이 있습니다.</p>
      </article>
    </section>

    <section class="home-projects">
      <div class="split-header">
        <div>
          <span class="eyebrow">Sample Projects</span>
          <h2 class="section-title">샘플 프로젝트</h2>
          <p class="muted">샘플 데이터를 열어 전체 흐름을 바로 확인할 수 있습니다.</p>
        </div>
        <NuxtLink class="ghost-button" to="/travels">전체 보기</NuxtLink>
      </div>

      <div v-if="error" class="error-box">
        프로젝트 목록을 불러오지 못했습니다. 백엔드 실행 상태를 확인해주세요.
      </div>

      <div v-else-if="featuredTravels.length" class="content-grid">
        <TravelCard v-for="travel in featuredTravels" :key="travel.id" :travel="travel" />
      </div>

      <EmptyState
        v-else
        title="아직 프로젝트가 없습니다"
        description="새 여행을 만들면 사진 업로드부터 프리뷰와 주문 흐름까지 바로 확인할 수 있습니다."
        action-label="새 여행 만들기"
        action-to="/travels/new"
      />
    </section>
  </div>
</template>
