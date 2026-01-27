import type { FormEvent } from 'react'
import { useEffect, useMemo, useState } from 'react'
import {
  Alert,
  Box,
  Button,
  MenuItem,
  Paper,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import { createReservation } from '../../api/reservations'
import type { Room } from '../../api/types'
import { datetimeLocalToIso } from '../../utils/datetime'
import { ApiRequestError } from '../../api/http'

type Props = {
  rooms: Room[]
  onCreated: () => void
}

const MINUTE_OPTIONS = ['00', '15', '30', '45'] as const
const HOUR_OPTIONS = Array.from({ length: 24 }, (_, i) => String(i).padStart(2, '0'))

function buildDateTimeLocal(date: string, hour: string, minute: string): string {
  if (!date || !hour || !minute) return ''
  return `${date}T${hour}:${minute}`
}

export function CreateReservationForm({ rooms, onCreated }: Props) {
  const [roomId, setRoomId] = useState<number | ''>('')
  const [title, setTitle] = useState('')
  const [startsDate, setStartsDate] = useState('')
  const [startsHour, setStartsHour] = useState('')
  const [startsMinute, setStartsMinute] = useState<(typeof MINUTE_OPTIONS)[number]>('00')
  const [endsDate, setEndsDate] = useState('')
  const [endsHour, setEndsHour] = useState('')
  const [endsMinute, setEndsMinute] = useState<(typeof MINUTE_OPTIONS)[number]>('00')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  const [participantCount, setParticipantCount] = useState<number | ''>('')

  const startsAt = useMemo(
    () => buildDateTimeLocal(startsDate, startsHour, startsMinute),
    [startsDate, startsHour, startsMinute],
  )

  const endsAt = useMemo(
    () => buildDateTimeLocal(endsDate, endsHour, endsMinute),
    [endsDate, endsHour, endsMinute],
  )

  const filteredRooms = useMemo(() => {
    if (participantCount === '') {
      return rooms
    }
    return rooms.filter((room) => room.capacity >= participantCount)
  }, [rooms, participantCount])

  useEffect(() => {
    if (roomId === '' || participantCount === '') {
      return
    }
    const selectedRoom = rooms.find((r) => r.id === roomId)
    if (!selectedRoom) {
      setRoomId('')
      return
    }
    if (selectedRoom.capacity < participantCount) {
      setRoomId('')
    }
  }, [roomId, participantCount, rooms])

  const canSubmit = useMemo(() => {
    return roomId !== '' && title.trim() !== '' && startsAt !== '' && endsAt !== '' && participantCount !== ''
  }, [roomId, title, startsAt, endsAt, participantCount])

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setSuccess(null)

    if (!canSubmit) {
      setError('Täytä kaikki kentät.')
      return
    }

    const startsIso = datetimeLocalToIso(startsAt)
    const endsIso = datetimeLocalToIso(endsAt)

    if (new Date(endsIso).getTime() <= new Date(startsIso).getTime()) {
      setError('Päättymisajan pitää olla alkamisajan jälkeen.')
      return
    }

    if (new Date(startsIso).getTime() < Date.now()) {
      setError('Varauksen alkamisaika ei voi olla menneisyydessä.')
      return
    }

    setSubmitting(true)
    try {
      await createReservation({
        roomId: Number(roomId),
        title: title.trim(),
        startsAt: startsIso,
        endsAt: endsIso,
        participantCount: Number(participantCount)
      })
      setSuccess('Varaus luotu.')
      setTitle('')
      setStartsDate('')
      setStartsHour('')
      setStartsMinute('00')
      setEndsDate('')
      setEndsHour('')
      setEndsMinute('00')
      setParticipantCount('')
      onCreated()
    } catch (err) {

      if (err instanceof ApiRequestError && err.status === 409) {
        setError('Tila on jo varattu valitulle ajalle.')
        setSubmitting(false)
        return
      }
      const message = err instanceof Error ? err.message : 'Varauksen luonti epäonnistui'
      setError(message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <>
      <Typography variant="h6" gutterBottom>
        Uusi varaus
      </Typography>

      <Paper variant="outlined" sx={{ p: 2 }}>
        <Box component="form" onSubmit={handleSubmit} noValidate name="create-reservation-form">
          <Stack spacing={2}>
            {error && <Alert severity="error">{error}</Alert>}
            {success && <Alert severity="success">{success}</Alert>}

            <TextField
              label="Osallistujamäärä"
              type="number"
              
              value={participantCount}
              onChange={(e) => setParticipantCount(e.target.value === '' ? '' : Number(e.target.value))}
              required
              fullWidth
            /> 

            <TextField
              select
              label="Tila"
              value={roomId}
              onChange={(e) => setRoomId(e.target.value === '' ? '' : Number(e.target.value))}
              required
              fullWidth
            >
              <MenuItem value="">Valitse tila</MenuItem>
              {filteredRooms.map((room) => (
                <MenuItem key={room.id} value={room.id}>
                  {room.name} (max {room.capacity})
                </MenuItem>
              ))}
            </TextField>

            <TextField
              label="Otsikko"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
              fullWidth
            />

            <TextField
              label="Alkaa"
              type="date"
              value={startsDate}
              onChange={(e) => setStartsDate(e.target.value)}
              required
              fullWidth
              InputLabelProps={{ shrink: true }}
            />

            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField
                select
                label="Alkaa (tunti)"
                value={startsHour}
                onChange={(e) => setStartsHour(e.target.value)}
                required
                fullWidth
              >
                <MenuItem value="">Valitse</MenuItem>
                {HOUR_OPTIONS.map((h) => (
                  <MenuItem key={h} value={h}>
                    {h}
                  </MenuItem>
                ))}
              </TextField>

              <TextField
                select
                label="Alkaa (min)"
                value={startsMinute}
                onChange={(e) => setStartsMinute(e.target.value as (typeof MINUTE_OPTIONS)[number])}
                required
                fullWidth
              >
                {MINUTE_OPTIONS.map((m) => (
                  <MenuItem key={m} value={m}>
                    {m}
                  </MenuItem>
                ))}
              </TextField>
            </Stack>

            <TextField
              label="Päättyy"
              type="date"
              value={endsDate}
              onChange={(e) => setEndsDate(e.target.value)}
              required
              fullWidth
              InputLabelProps={{ shrink: true }}
            />

            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField
                select
                label="Päättyy (tunti)"
                value={endsHour}
                onChange={(e) => setEndsHour(e.target.value)}
                required
                fullWidth
              >
                <MenuItem value="">Valitse</MenuItem>
                {HOUR_OPTIONS.map((h) => (
                  <MenuItem key={h} value={h}>
                    {h}
                  </MenuItem>
                ))}
              </TextField>

              <TextField
                select
                label="Päättyy (min)"
                value={endsMinute}
                onChange={(e) => setEndsMinute(e.target.value as (typeof MINUTE_OPTIONS)[number])}
                required
                fullWidth
              >
                {MINUTE_OPTIONS.map((m) => (
                  <MenuItem key={m} value={m}>
                    {m}
                  </MenuItem>
                ))}
              </TextField>
            </Stack>

            <Button type="submit" variant="contained" disabled={!canSubmit || submitting}>
              Luo varaus
            </Button>
          </Stack>
        </Box>
      </Paper>
    </>
  )
}
