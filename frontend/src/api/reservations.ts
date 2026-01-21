import { apiFetch } from './http'
import type { CreateReservationRequest, Reservation } from './types'

export async function listReservations(): Promise<Reservation[]> {
  return apiFetch<Reservation[]>('/reservations')
}

export async function createReservation(
  request: CreateReservationRequest,
): Promise<Reservation> {
  return apiFetch<Reservation>('/reservations', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  })
}

export async function deleteReservation(id: number): Promise<void> {
  await apiFetch<void>(`/reservations/${id}`, {
    method: 'DELETE',
    headers: {
      Accept: 'application/json',
    },
  })
}
