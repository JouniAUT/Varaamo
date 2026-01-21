import { apiFetch } from './http'
import type { Room } from './types'

export async function listRooms(): Promise<Room[]> {
  return apiFetch<Room[]>('/rooms')
}
