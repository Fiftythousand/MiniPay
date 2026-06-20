<template>
  <div id="app">
    <nav class="navbar">
      <div class="navbar-brand">
        <h1>MiniPay</h1>
      </div>
      <div class="navbar-nav">
        <router-link to="/" :class="{ active: $route.name === 'CreateOrder' }">创建订单</router-link>
        <router-link to="/query" :class="{ active: $route.name === 'QueryResult' }">结果查询</router-link>
      </div>
    </nav>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: #f5f5f5;
  color: #333;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.navbar-brand h1 {
  font-size: 1.5rem;
  font-weight: 600;
}

.navbar-nav {
  display: flex;
  gap: 2rem;
}

.navbar-nav a {
  color: white;
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background 0.3s;
}

.navbar-nav a:hover,
.navbar-nav a.active {
  background: rgba(255,255,255,0.2);
}

.main-content {
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
}
</style>