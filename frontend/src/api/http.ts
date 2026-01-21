import type { ApiError } from './types'

export class ApiRequestError extends Error {
  readonly status: number
  readonly code?: string

  constructor(message: string, status: number, code?: string) {
    super(message)
    this.name = 'ApiRequestError'
    this.status = status
    this.code = code
  }
}

async function tryReadJson(response: Response): Promise<unknown | undefined> {
  const contentType = response.headers.get('content-type') || ''
  if (!contentType.includes('application/json')) return undefined
  try {
    return await response.json()
  } catch {
    return undefined
  }
}

export async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    ...init,
    headers: {
      Accept: 'application/json',
      ...(init?.headers ?? {}),
    },
  })

  if (response.ok) {
    if (response.status === 204) {
      return undefined as T
    }
    return (await response.json()) as T
  }

  const body = await tryReadJson(response)
  const apiError = body as Partial<ApiError> | undefined

  const message =
    (apiError && typeof apiError.message === 'string' && apiError.message) ||
    `Pyyntö epäonnistui (HTTP ${response.status})`

  const code = apiError && typeof apiError.code === 'string' ? apiError.code : undefined

  throw new ApiRequestError(message, response.status, code)
}
