<template>
  <main class="page">
    <section class="card auth-card">
      <!-- Logo / 图标 -->
      <div class="auth-logo">
        <svg class="icon logo-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
          <path d="M9 11l3 3L22 4" />
          <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
        </svg>
      </div>
      <h1>待办清单</h1>
      <p class="subtitle">登录后管理你的任务</p>

      <form class="form" @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="username">用户名</label>
          <div class="input-wrapper">
            <svg class="icon input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
            <input id="username" v-model="username" type="text" placeholder="请输入用户名" autocomplete="username" />
          </div>
        </div>
        <div class="input-group">
          <label for="password">密码</label>
          <div class="input-wrapper">
            <svg class="icon input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
            </svg>
            <input id="password" v-model="password" type="password" placeholder="请输入密码" autocomplete="current-password" />
          </div>
        </div>
        <p v-if="errorMessage" class="error">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10" />
            <line x1="15" y1="9" x2="9" y2="15" />
            <line x1="9" y1="9" x2="15" y2="15" />
          </svg>
          {{ errorMessage }}
        </p>
        <button :disabled="loading" type="submit" class="login-btn">
          <template v-if="loading">
            <span class="spinner"></span>
            登录中...
          </template>
          <template v-else>
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4" />
              <polyline points="10 17 15 12 10 7" />
              <line x1="15" y1="12" x2="3" y2="12" />
            </svg>
            登录
          </template>
        </button>
      </form>
    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'

const router = useRouter()
const auth = useAuthStore()
const theme = useThemeStore()

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const loading = ref(false)

theme.setTheme(theme.theme)

async function handleLogin() {
  errorMessage.value = ''
  loading.value = true
  try {
    await auth.login(username.value, password.value)
    await router.push('/todos')
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-card {
  max-width: 400px;
  margin: 60px auto;
  text-align: center;
  padding: 44px 36px 40px;
}

/* ── Logo ── */
.auth-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.logo-icon {
  width: 48px;
  height: 48px;
  color: var(--accent);
  background: var(--accent-light);
  padding: 10px;
  border-radius: 16px;
}

.auth-card h1 {
  font-size: 26px;
  font-weight: 800;
  margin: 0 0 4px;
  color: var(--text-primary);
  letter-spacing: -0.3px;
}

.subtitle {
  color: var(--text-muted);
  margin: 0 0 32px;
  font-size: 14px;
  font-weight: 500;
}

/* ── 表单 ── */
.form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.input-group {
  text-align: left;
}

.input-group label {
  display: block;
  font-size: 13px;
  font-weight: 700;
  color: var(--text-muted);
  margin-bottom: 6px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  width: 18px;
  height: 18px;
  color: var(--text-muted);
  pointer-events: none;
  opacity: 0.6;
}

.input-wrapper input {
  width: 100%;
  border: 2px solid var(--border-color);
  border-radius: 14px;
  padding: 12px 16px 12px 42px;
  background: var(--input-bg);
  color: var(--text-primary);
  font-size: 15px;
  font-family: inherit;
  transition: border-color 0.25s ease, box-shadow 0.25s ease;
  outline: none;
}

.input-wrapper input:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 4px var(--accent-light);
}

.input-wrapper input:focus ~ .input-icon,
.input-wrapper input:focus + .input-icon {
  color: var(--accent);
  opacity: 1;
}

.input-wrapper input::placeholder {
  color: var(--text-muted);
  opacity: 0.5;
}

/* ── 错误 ── */
.error {
  color: var(--danger);
  margin: 0;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(232, 138, 138, 0.08);
  padding: 10px 14px;
  border-radius: 12px;
}

.error .icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

/* ── 登录按钮 ── */
.login-btn {
  border: 0;
  border-radius: 14px;
  padding: 13px 14px;
  cursor: pointer;
  color: #fff;
  background: var(--accent);
  font-size: 16px;
  font-weight: 700;
  font-family: inherit;
  transition: background 0.25s ease, transform 0.15s ease, box-shadow 0.25s ease;
  margin-top: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-btn:hover:not(:disabled) {
  background: var(--accent-hover);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(108, 158, 191, 0.3);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.login-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.login-btn .icon {
  width: 18px;
  height: 18px;
}

/* ── 加载旋转 ── */
.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
