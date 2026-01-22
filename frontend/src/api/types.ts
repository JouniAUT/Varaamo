export type ApiError = {
  code: string
  message: string
  timestamp: string
}

export type Room = {
  id: number
  name: string
  capacity: number
}

export type Reservation = {
  id: number
  roomId: number
  startsAt: string
  endsAt: string
  title: string
}

export type CreateReservationRequest = {
  roomId: number
  startsAt: string
  endsAt: string
  title: string
}
