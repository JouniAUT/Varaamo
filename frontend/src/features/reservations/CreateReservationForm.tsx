import type { FormEvent } from 'react'
import { useMemo, useState } from 'react'
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

type Props = {
  rooms: Room[]
  onCreated: () => void
}

export function CreateReservationForm({ rooms, onCreated }: Props) {
  const [roomId, setRoomId] = useState<number | ''>('')
  const [title, setTitle] = useState('')
  const [startsAt, setStartsAt] = useState('')
  const [endsAt, setEndsAt] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  const canSubmit = useMemo(() => {
    return roomId !== '' && title.trim() !== '' && startsAt !== '' && endsAt !== ''
  }, [roomId, title, startsAt, endsAt])

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

    setSubmitting(true)
    try {
      await createReservation({
        roomId: Number(roomId),
        title: title.trim(),
        startsAt: startsIso,
        endsAt: endsIso,
      })
      setSuccess('Varaus luotu.')
      setTitle('')
      setStartsAt('')
      setEndsAt('')
      onCreated()
    } catch (err) {
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
        <Box component="form" onSubmit={handleSubmit} noValidate>
          <Stack spacing={2}>
            {error && <Alert severity="error">{error}</Alert>}
            {success && <Alert severity="success">{success}</Alert>}

            <TextField
              select
              label="Tila"
              value={roomId}
              onChange={(e) => setRoomId(e.target.value === '' ? '' : Number(e.target.value))}
              required
              fullWidth
            >
              <MenuItem value="">Valitse tila</MenuItem>
              {rooms.map((room) => (
                <MenuItem key={room.id} value={room.id}>
                  {room.name}
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
              type="datetime-local"
              value={startsAt}
              onChange={(e) => setStartsAt(e.target.value)}
              required
              fullWidth
              InputLabelProps={{ shrink: true }}
            />

            <TextField
              label="Päättyy"
              type="datetime-local"
              value={endsAt}
              onChange={(e) => setEndsAt(e.target.value)}
              required
              fullWidth
              InputLabelProps={{ shrink: true }}
            />

            <Button type="submit" variant="contained" disabled={!canSubmit || submitting}>
              Luo varaus
            </Button>
          </Stack>
        </Box>
      </Paper>
    </>
  )
}
