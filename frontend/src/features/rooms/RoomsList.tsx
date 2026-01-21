import {
  Alert,
  CircularProgress,
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material'
import type { Room } from '../../api/types'

type Props = {
  rooms: Room[]
  loading: boolean
  error: string | null
  onRefresh: () => void
}

export function RoomsList({ rooms, loading, error, onRefresh }: Props) {
  return (
    <>
      <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
        <span style={{ flexGrow: 1 }}>Tilat</span>
        <Button onClick={onRefresh} disabled={loading} size="small" variant="outlined">
          Päivitä
        </Button>
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {loading && rooms.length === 0 ? (
        <CircularProgress />
      ) : (
        <TableContainer component={Paper} variant="outlined">
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Nimi</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rooms.map((room) => (
                <TableRow key={room.id} hover>
                  <TableCell width={100}>{room.id}</TableCell>
                  <TableCell>{room.name}</TableCell>
                </TableRow>
              ))}
              {rooms.length === 0 && (
                <TableRow>
                  <TableCell colSpan={2}>Ei tiloja.</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </>
  )
}
