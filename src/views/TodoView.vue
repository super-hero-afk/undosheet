<template>
  <main class="page">
    <section class="card">
      <!-- 顶部工具栏 -->
      <header class="toolbar">
        <div class="toolbar-left">
          <h1>
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 11l3 3L22 4" />
              <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
            </svg>
            待办清单
          </h1>
          <p class="subtitle">你好，{{ auth.user?.username }}</p>
        </div>
        <div class="toolbar-actions">
          <button class="ghost" @click="theme.toggleTheme" :title="theme.isDark ? '切换亮色' : '切换暗色'">
            <svg v-if="theme.isDark" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="5" />
              <line x1="12" y1="1" x2="12" y2="3" />
              <line x1="12" y1="21" x2="12" y2="23" />
              <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
              <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
              <line x1="1" y1="12" x2="3" y2="12" />
              <line x1="21" y1="12" x2="23" y2="12" />
              <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
              <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
            </svg>
            <svg v-else class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
            </svg>
          </button>
          <button class="danger" @click="handleLogout">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
            退出
          </button>
        </div>
      </header>

      <!-- 添加表单 -->
      <form class="todo-form" @submit.prevent="createTodo">
        <div class="input-wrapper">
          <svg class="icon input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          <input v-model="newTodoTitle" type="text" placeholder="输入新的待办事项..." />
        </div>
        <select v-model="newTodoCategory">
          <option v-for="option in categoryOptionsWithoutAll" :key="option" :value="option">
            {{ option }}
          </option>
        </select>
        <button type="submit" :disabled="!newTodoTitle.trim()">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 11l3 3L22 4" />
            <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
          </svg>
          添加
        </button>
      </form>

      <!-- 筛选栏 -->
      <section class="filters">
        <div class="filter-select-wrapper">
          <svg class="icon filter-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <line x1="4" y1="21" x2="4" y2="14" />
            <line x1="4" y1="10" x2="4" y2="3" />
            <line x1="12" y1="21" x2="12" y2="12" />
            <line x1="12" y1="8" x2="12" y2="3" />
            <line x1="20" y1="21" x2="20" y2="16" />
            <line x1="20" y1="12" x2="20" y2="3" />
            <line x1="1" y1="14" x2="7" y2="14" />
            <line x1="9" y1="8" x2="15" y2="8" />
            <line x1="17" y1="16" x2="23" y2="16" />
          </svg>
          <select v-model="todoStore.selectedCategory">
            <option v-for="option in CATEGORY_OPTIONS" :key="option" :value="option">{{ option }}</option>
          </select>
        </div>
        <div class="input-wrapper">
          <svg class="icon input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8" />
            <line x1="21" y1="21" x2="16.65" y2="16.65" />
          </svg>
          <input v-model="todoStore.keyword" type="text" placeholder="搜索待办..." />
        </div>
      </section>

      <!-- 错误消息 -->
      <transition name="msg">
        <p v-if="errorMessage" class="error">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10" />
            <line x1="15" y1="9" x2="9" y2="15" />
            <line x1="9" y1="9" x2="15" y2="15" />
          </svg>
          {{ errorMessage }}
        </p>
      </transition>

      <!-- 统计 -->
      <section class="stats">
        <span>
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 11l3 3L22 4" />
            <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
          </svg>
          全部 {{ todoStore.stats.total }}
        </span>
        <span class="stat-done">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
            <polyline points="22 4 12 14.01 9 11.01" />
          </svg>
          已完成 {{ todoStore.stats.done }}
        </span>
        <span class="stat-pending">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10" />
            <polyline points="12 6 12 12 16 14" />
          </svg>
          未完成 {{ todoStore.stats.pending }}
        </span>
      </section>

      <!-- 加载骨架屏 -->
      <template v-if="todoStore.loading">
        <ul class="todo-list">
          <li v-for="n in 3" :key="n" class="todo-item skeleton-item">
            <div class="skeleton skeleton-checkbox"></div>
            <div class="todo-main">
              <div class="skeleton skeleton-title"></div>
              <div class="skeleton skeleton-badge"></div>
            </div>
          </li>
        </ul>
      </template>

      <!-- 待办列表 -->
      <ul v-else-if="todoStore.filteredTodos.length" class="todo-list">
        <li v-for="(todo, index) in todoStore.filteredTodos" :key="todo.id" class="todo-item" :style="{ '--i': index }">
          <input :checked="todo.completed" type="checkbox" @change="toggleTodo(todo.id)" :id="'todo-' + todo.id" />
          <label :for="'todo-' + todo.id" class="checkbox-label">
            <svg class="icon check-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="20 6 9 17 4 12" />
            </svg>
          </label>
          <div class="todo-main">
            <template v-if="editingId === todo.id">
              <input v-model="editingTitle" type="text" class="edit-input" @keyup.enter="saveEdit(todo.id)" @keyup.esc="cancelEdit" ref="editInput" />
              <select v-model="editingCategory" class="edit-select">
                <option v-for="option in categoryOptionsWithoutAll" :key="option" :value="option">{{ option }}</option>
              </select>
            </template>
            <template v-else>
              <p :class="{ done: todo.completed }">{{ todo.title }}</p>
              <span class="badge" :data-category="todo.category">{{ todo.category }}</span>
            </template>
          </div>
          <div class="todo-actions">
            <button v-if="editingId === todo.id" class="ghost" @click="saveEdit(todo.id)" title="保存">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z" />
                <polyline points="17 21 17 13 7 13 7 21" />
                <polyline points="7 3 7 8 15 8" />
              </svg>
            </button>
            <button v-if="editingId === todo.id" class="ghost" @click="cancelEdit" title="取消">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18" />
                <line x1="6" y1="6" x2="18" y2="18" />
              </svg>
            </button>
            <button v-else class="ghost" @click="startEdit(todo)" title="编辑">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
              </svg>
            </button>
            <button class="danger" @click="removeTodo(todo.id)" title="删除">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="3 6 5 6 21 6" />
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                <line x1="10" y1="11" x2="10" y2="17" />
                <line x1="14" y1="11" x2="14" y2="17" />
              </svg>
            </button>
          </div>
        </li>
      </ul>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <svg class="empty-icon" viewBox="0 0 80 80" fill="none">
          <rect x="10" y="18" width="60" height="50" rx="8" stroke="currentColor" stroke-width="2" fill="none" class="empty-box" />
          <line x1="22" y1="34" x2="58" y2="34" stroke="currentColor" stroke-width="2" stroke-linecap="round" class="empty-line" />
          <line x1="22" y1="44" x2="48" y2="44" stroke="currentColor" stroke-width="2" stroke-linecap="round" class="empty-line" />
          <line x1="22" y1="54" x2="38" y2="54" stroke="currentColor" stroke-width="2" stroke-linecap="round" class="empty-line" />
          <circle cx="58" cy="52" r="14" fill="var(--accent-light)" stroke="var(--accent)" stroke-width="2" />
          <line x1="58" y1="46" x2="58" y2="58" stroke="var(--accent)" stroke-width="2.5" stroke-linecap="round" />
          <line x1="52" y1="52" x2="64" y2="52" stroke="var(--accent)" stroke-width="2.5" stroke-linecap="round" />
        </svg>
        <p class="empty-title">{{ todoStore.keyword ? '没有匹配的待办' : '还没有待办事项' }}</p>
        <p class="empty-desc">{{ todoStore.keyword ? '试试其他关键词吧' : '在上方输入框添加你的第一个任务' }}</p>
      </div>
    </section>
  </main>
</template>

<script setup>
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { CATEGORY_OPTIONS, useTodoStore } from '@/stores/todos'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'

const router = useRouter()
const auth = useAuthStore()
const todoStore = useTodoStore()
const theme = useThemeStore()

const newTodoTitle = ref('')
const newTodoCategory = ref('工作')
const editingId = ref(null)
const editingTitle = ref('')
const editingCategory = ref('工作')
const errorMessage = ref('')
const editInput = ref(null)

const categoryOptionsWithoutAll = computed(() => CATEGORY_OPTIONS.filter((item) => item !== '全部'))

async function createTodo() {
  errorMessage.value = ''
  try {
    await todoStore.addTodo(newTodoTitle.value, newTodoCategory.value)
    newTodoTitle.value = ''
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '添加失败'
  }
}

function startEdit(todo) {
  editingId.value = todo.id
  editingTitle.value = todo.title
  editingCategory.value = todo.category
  nextTick(() => {
    if (editInput.value) editInput.value.focus()
  })
}

async function saveEdit(id) {
  errorMessage.value = ''
  try {
    await todoStore.updateTodo(id, {
      title: editingTitle.value,
      category: editingCategory.value
    })
    cancelEdit()
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '保存失败'
  }
}

function cancelEdit() {
  editingId.value = null
  editingTitle.value = ''
  editingCategory.value = '工作'
}

async function handleLogout() {
  await auth.logout()
  await router.push('/login')
}

async function removeTodo(id) {
  errorMessage.value = ''
  try {
    await todoStore.removeTodo(id)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '删除失败'
  }
}

async function toggleTodo(id) {
  errorMessage.value = ''
  try {
    await todoStore.toggleTodo(id)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '状态更新失败'
  }
}

onMounted(async () => {
  await todoStore.fetchTodos()
})

watch([() => todoStore.selectedCategory, () => todoStore.keyword], async () => {
  await todoStore.fetchTodos()
})
</script>

<style scoped>
/* ── 工具栏 ── */
.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 20px;
}

.toolbar-left h1 {
  font-size: 24px;
  font-weight: 800;
  margin: 0;
  letter-spacing: -0.3px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.toolbar-left h1 .icon {
  width: 26px;
  height: 26px;
  color: var(--accent);
}

.subtitle {
  color: var(--text-muted);
  margin: 2px 0 0;
  font-size: 14px;
  font-weight: 500;
}

.toolbar-actions {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.toolbar-actions button {
  border: 0;
  border-radius: 12px;
  padding: 8px 14px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
  font-family: inherit;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.toolbar-actions button .icon {
  width: 16px;
  height: 16px;
}

.toolbar-actions button.ghost {
  background: var(--btn-muted-bg);
  color: var(--text-primary);
}

.toolbar-actions button.ghost:hover {
  background: var(--border-color);
}

.toolbar-actions button.danger {
  background: var(--danger);
  color: #fff;
}

.toolbar-actions button.danger:hover {
  background: var(--danger-hover);
}

/* ── 添加表单 ── */
.todo-form {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.todo-form .input-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
}

.todo-form .input-icon {
  position: absolute;
  left: 14px;
  width: 18px;
  height: 18px;
  color: var(--text-muted);
  pointer-events: none;
  opacity: 0.5;
  transition: opacity 0.25s ease;
}

.todo-form .input-wrapper:focus-within .input-icon {
  opacity: 1;
  color: var(--accent);
}

.todo-form input {
  width: 100%;
  border: 2px solid var(--border-color);
  border-radius: 14px;
  padding: 12px 16px 12px 42px;
  background: var(--input-bg);
  color: var(--text-primary);
  font-size: 15px;
  font-family: inherit;
  outline: none;
  transition: border-color 0.25s ease, box-shadow 0.25s ease;
}

.todo-form input:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 4px var(--accent-light);
}

.todo-form input::placeholder {
  color: var(--text-muted);
  opacity: 0.5;
}

.todo-form select {
  border: 2px solid var(--border-color);
  border-radius: 14px;
  padding: 12px 14px;
  background: var(--input-bg);
  color: var(--text-primary);
  font-size: 14px;
  font-family: inherit;
  outline: none;
  cursor: pointer;
  transition: border-color 0.25s ease;
  min-width: 100px;
}

.todo-form select:focus {
  border-color: var(--accent);
}

.todo-form button {
  border: 0;
  border-radius: 14px;
  padding: 12px 20px;
  cursor: pointer;
  color: #fff;
  background: var(--accent);
  font-size: 15px;
  font-weight: 700;
  font-family: inherit;
  transition: background 0.25s ease, transform 0.15s ease, box-shadow 0.25s ease, opacity 0.25s ease;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 6px;
}

.todo-form button:hover:not(:disabled) {
  background: var(--accent-hover);
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(108, 158, 191, 0.3);
}

.todo-form button:active:not(:disabled) {
  transform: translateY(0);
}

.todo-form button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.todo-form button .icon {
  width: 16px;
  height: 16px;
}

/* ── 筛选栏 ── */
.filters {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.filter-select-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.filter-icon {
  position: absolute;
  left: 12px;
  width: 16px;
  height: 16px;
  color: var(--text-muted);
  pointer-events: none;
  opacity: 0.5;
}

.filter-select-wrapper select {
  border: 2px solid var(--border-color);
  border-radius: 12px;
  padding: 10px 14px 10px 36px;
  background: var(--input-bg);
  color: var(--text-primary);
  font-size: 14px;
  font-family: inherit;
  outline: none;
  cursor: pointer;
  transition: border-color 0.25s ease;
  min-width: 120px;
  appearance: none;
  -webkit-appearance: none;
}

.filter-select-wrapper select:focus {
  border-color: var(--accent);
}

.filters .input-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
}

.filters .input-icon {
  position: absolute;
  left: 12px;
  width: 16px;
  height: 16px;
  color: var(--text-muted);
  pointer-events: none;
  opacity: 0.5;
  transition: opacity 0.25s ease;
}

.filters .input-wrapper:focus-within .input-icon {
  opacity: 1;
  color: var(--accent);
}

.filters input {
  width: 100%;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  padding: 10px 14px 10px 36px;
  background: var(--input-bg);
  color: var(--text-primary);
  font-size: 14px;
  font-family: inherit;
  outline: none;
  transition: border-color 0.25s ease, box-shadow 0.25s ease;
}

.filters input:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 4px var(--accent-light);
}

.filters input::placeholder {
  color: var(--text-muted);
  opacity: 0.5;
}

/* ── 统计 ── */
.stats {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 5px;
  background: var(--btn-muted-bg);
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  color: var(--text-muted);
}

.stats .icon {
  width: 14px;
  height: 14px;
}

.stat-done .icon { color: var(--success); }
.stat-pending .icon { color: var(--accent); }

/* ── 错误消息 ── */
.error {
  color: var(--danger);
  margin: 0 0 12px;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: rgba(232, 138, 138, 0.08);
  border-radius: 12px;
}

.error .icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.msg-enter-active,
.msg-leave-active {
  transition: all 0.3s ease;
}

.msg-enter-from,
.msg-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ── 待办列表 ── */
.todo-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.todo-item {
  display: flex;
  gap: 4px;
  align-items: center;
  border: 2px solid var(--border-color);
  border-radius: 16px;
  padding: 10px 14px 10px 10px;
  background: var(--bg-secondary);
  transition: border-color 0.2s ease, box-shadow 0.2s ease, opacity 0.3s ease;
  animation: fadeSlideUp 0.35s ease both;
  animation-delay: calc(var(--i, 0) * 0.05s);
}

.todo-item:hover {
  border-color: var(--accent-light);
  box-shadow: 0 2px 12px rgba(108, 158, 191, 0.08);
}

/* ── 自定义复选框 ── */
.todo-item input[type='checkbox'] {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
  pointer-events: none;
}

.checkbox-label {
  width: 24px;
  height: 24px;
  border: 2px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
  background: var(--input-bg);
}

.checkbox-label .check-icon {
  width: 14px;
  height: 14px;
  color: transparent;
  transition: color 0.25s ease;
}

.todo-item input[type='checkbox']:checked + .checkbox-label {
  background: var(--success);
  border-color: var(--success);
}

.todo-item input[type='checkbox']:checked + .checkbox-label .check-icon {
  color: #fff;
}

.checkbox-label:hover {
  border-color: var(--accent);
}

/* ── 待办内容 ── */
.todo-main {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  padding: 0 6px;
}

.todo-main p {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.todo-main .done {
  text-decoration: line-through;
  color: var(--text-muted);
  font-weight: 500;
}

.edit-input {
  border: 2px solid var(--accent);
  border-radius: 10px;
  padding: 8px 12px;
  background: var(--input-bg);
  color: var(--text-primary);
  font-size: 14px;
  font-family: inherit;
  outline: none;
  flex: 1;
  min-width: 100px;
}

.edit-select {
  border: 2px solid var(--accent);
  border-radius: 10px;
  padding: 8px 10px;
  background: var(--input-bg);
  color: var(--text-primary);
  font-size: 13px;
  font-family: inherit;
  outline: none;
  cursor: pointer;
}

/* ── 分类标签 ── */
.badge {
  font-size: 12px;
  font-weight: 700;
  border-radius: 999px;
  padding: 3px 12px;
  flex-shrink: 0;
}

.badge[data-category='工作'] {
  background: var(--tag-work);
  color: #8a7a5a;
}

.badge[data-category='生活'] {
  background: var(--tag-life);
  color: #5a7a8a;
}

.badge[data-category='学习'] {
  background: var(--tag-study);
  color: #7a5a8a;
}

/* ── 操作按钮 ── */
.todo-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.todo-actions button {
  border: 0;
  border-radius: 10px;
  padding: 6px 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
  font-family: inherit;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}

.todo-actions button .icon {
  width: 16px;
  height: 16px;
}

.todo-actions button.ghost {
  background: transparent;
  color: var(--text-muted);
}

.todo-actions button.ghost:hover {
  background: var(--btn-muted-bg);
  color: var(--text-primary);
}

.todo-actions button.danger {
  background: transparent;
  color: var(--text-muted);
}

.todo-actions button.danger:hover {
  background: rgba(232, 138, 138, 0.1);
  color: var(--danger);
}

/* ── 骨架屏 ── */
.skeleton-item {
  border-color: transparent !important;
  background: transparent !important;
  animation: none !important;
  padding: 12px 14px 12px 10px;
}

.skeleton-checkbox {
  width: 24px;
  height: 24px;
  border-radius: 8px;
  flex-shrink: 0;
}

.skeleton-title {
  height: 18px;
  flex: 1;
  border-radius: 6px;
}

.skeleton-badge {
  width: 48px;
  height: 20px;
  border-radius: 10px;
  flex-shrink: 0;
}

/* ── 空状态 ── */
.empty-state {
  text-align: center;
  padding: 32px 0 24px;
  animation: fadeIn 0.4s ease;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
  color: var(--text-muted);
  opacity: 0.5;
}

.empty-box,
.empty-line {
  stroke: var(--text-muted);
  opacity: 0.3;
}

.empty-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-muted);
  margin: 0 0 4px;
}

.empty-desc {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-muted);
  margin: 0;
  opacity: 0.7;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .card {
    padding: 20px 16px;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-actions {
    justify-content: flex-end;
  }

  .todo-form {
    flex-direction: column;
  }

  .filters {
    flex-direction: column;
  }

  .todo-item {
    flex-wrap: wrap;
    gap: 8px;
  }

  .todo-main {
    min-width: calc(100% - 80px);
  }

  .todo-actions {
    width: 100%;
    justify-content: flex-end;
    padding-right: 4px;
  }
}

/* ── 复用全局动画 ── */
@keyframes fadeSlideUp {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
