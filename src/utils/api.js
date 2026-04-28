const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081'

export async function apiFetch(path, options = {}) {
  const { headers: extraHeaders, ...restOptions } = options
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...restOptions,
    headers: {
      'Content-Type': 'application/json',
      ...(extraHeaders || {})
    }
  })

  if (response.status === 204) return null

  const data = await response.json().catch(() => ({}))
  if (!response.ok) {
    throw new Error(data.message || '请求失败')
  }
  return data
}
