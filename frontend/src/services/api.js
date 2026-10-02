const API_BASE_URL = '/api'

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem('token')

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && {
        Authorization: `Bearer ${token}`,
      }),
      ...options.headers,
    },
  })

  let data = null

  if (response.status !== 204) {
    const responseText = await response.text()

    if (responseText) {
      try {
        data = JSON.parse(responseText)
      } catch {
        data = responseText
      }
    }
  }

  if (!response.ok) {
    throw new Error(
      data?.message || `Request failed with status ${response.status}`,
    )
  }

  return data
}
