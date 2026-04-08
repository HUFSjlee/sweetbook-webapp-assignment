<template>
  <div class="app-shell">
    <header class="site-header">
      <NuxtLink to="/" class="brand-mark">
        <span class="brand-mark__badge">SB</span>
        <span>
          <strong>TravelogBook</strong>
          <small>Printed travel stories, designed in motion.</small>
        </span>
      </NuxtLink>

      <nav class="main-nav">
        <NuxtLink to="/travels">Travels</NuxtLink>
        <NuxtLink to="/travels/new">New Project</NuxtLink>
        <NuxtLink to="/orders">Orders</NuxtLink>
      </nav>
    </header>

    <main class="page-shell">
      <slot />
    </main>
  </div>
</template>
