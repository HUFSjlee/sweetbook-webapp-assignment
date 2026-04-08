<template>
  <div class="app-shell">
    <header class="site-header">
      <NuxtLink to="/" class="brand-mark">
        <span>
          <strong>TravelogBook</strong>
          <small>당신의 여행을 한 권에 담아보세요</small>
        </span>
      </NuxtLink>

      <nav class="main-nav">
        <NuxtLink to="/travels">여행 목록</NuxtLink>
        <NuxtLink to="/travels/new">새 프로젝트</NuxtLink>
        <NuxtLink to="/orders">주문 내역</NuxtLink>
      </nav>
    </header>

    <main class="page-shell">
      <slot />
    </main>
  </div>
</template>
