import { useEffect, useMemo, useState } from 'react'
import {
  Alert,
  CircularProgress,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
  Typography,
} from '@mui/material'
import DeleteIcon from '@mui/icons-material/Delete'
import { deleteReservation, listReservations } from '../../api/reservations'
import type { Reservation, Room } from '../../api/types'
import { formatFinnishDateTime } from '../../utils/datetime'

type Props = {
  rooms: Room[]
  refreshToken: number
}

export function ReservationsList({ rooms, refreshToken }: Props) {
  const [reservations, setReservations] = useState<Reservation[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const roomNameById = useMemo(() => {
    const map = new Map<number, string>()
    rooms.forEach((r) => map.set(r.id, r.name))
    return map
  }, [rooms])

  useEffect(() => {
    let cancelled = false

    async function load() {
      setLoading(true)
      setError(null)
      try {
        const data = await listReservations()
        if (!cancelled) setReservations(data)
      } catch (e) {
        const message = e instanceof Error ? e.message : 'Varausten haku epäonnistui'
        if (!cancelled) setError(message)
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    void load()

    return () => {
      cancelled = true
    }
  }, [refreshToken])

  async function handleDelete(id: number) {
    if (!confirm('Poistetaanko varaus?')) return

    try {
      await deleteReservation(id)
      setReservations((prev) => prev.filter((r) => r.id !== id))
    } catch (e) {
      const message = e instanceof Error ? e.message : 'Poisto epäonnistui'
      setError(message)
    }
  }

  return (
    <>
      <Typography variant="h6" gutterBottom>
        Varaukset
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {loading ? (
        <CircularProgress />
      ) : (
        <TableContainer component={Paper} variant="outlined">
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Otsikko</TableCell>
                <TableCell>Osallistujamäärä</TableCell>
                <TableCell>Tila</TableCell>
                <TableCell>Alkaa</TableCell>
                <TableCell>Päättyy</TableCell>
                <TableCell align="right">Toiminnot</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {reservations.map((r) => (
                <TableRow key={r.id} hover>
                  <TableCell>{r.title}</TableCell>
                  <TableCell>{r.participantCount}</TableCell>
                  <TableCell>{roomNameById.get(r.roomId) ?? `#${r.roomId}`}</TableCell>
                  <TableCell>{formatFinnishDateTime(r.startsAt)}</TableCell>
                  <TableCell>{formatFinnishDateTime(r.endsAt)}</TableCell>
                  <TableCell align="right">
                    <Tooltip title="Poista">
                      <IconButton onClick={() => void handleDelete(r.id)} size="small">
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))}

              {reservations.length === 0 && !loading && (
                <TableRow>
                  <TableCell colSpan={6}>Ei varauksia.</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </>
  )
}
