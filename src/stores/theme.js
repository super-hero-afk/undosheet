import { computed, ref, watch } from 'vue'
import { defineStore } from 'pinia'

const THEME_KEY = 'todo-plus-theme'

function readSavedTheme() {
  const saved = localStorage.getItem(THEME_KEY)
  return saved === 'dark' ? 'dark' : 'light'
}

export const useThemeStore = defineStore('theme', () => {
  const theme = ref(readSavedTheme())
  const isDark = computed(() => theme.value === 'dark')

  function setTheme(nextTheme) {
    theme.value = nextTheme === 'dark' ? 'dark' : 'light'
  }

  function toggleTheme() {
    setTheme(isDark.value ? 'light' : 'dark')
  }

  watch(
    theme,
    (value) => {
      document.documentElement.setAttribute('data-theme', value)
      localStorage.setItem(THEME_KEY, value)
    },
    { immediate: true }
  )

  return {
    theme,
    isDark,
    setTheme,
    toggleTheme
  }
})
