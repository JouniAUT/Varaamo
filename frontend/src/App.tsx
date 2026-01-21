import { useCallback, useEffect, useMemo, useState } from 'react'
import { AppBar, Box, Container, Tab, Tabs, Toolbar, Typography } from '@mui/material'
import { listRooms } from './api/rooms'
import type { Room } from './api/types'
import { RoomsList } from './features/rooms/RoomsList'
import { ReservationsList } from './features/reservations/ReservationsList'
import { CreateReservationForm } from './features/reservations/CreateReservationForm'

function App() {
  const [tab, setTab] = useState(0)
  const [rooms, setRooms] = useState<Room[]>([])
  const [roomsLoading, setRoomsLoading] = useState(false)
  const [roomsError, setRoomsError] = useState<string | null>(null)
  const [reservationsRefreshToken, setReservationsRefreshToken] = useState(0)

  const loadRooms = useCallback(async () => {
    setRoomsLoading(true)
    setRoomsError(null)
    try {
      const data = await listRooms()
      setRooms(data)
    } catch (e) {
      const message = e instanceof Error ? e.message : 'Tilojen haku epäonnistui'
      setRoomsError(message)
    } finally {
      setRoomsLoading(false)
    }
  }, [])

  useEffect(() => {
    void loadRooms()
  }, [loadRooms])

  const canCreateReservation = useMemo(() => rooms.length > 0, [rooms.length])

  return (
    <Box sx={{ minHeight: '100%', bgcolor: 'background.default' }}>
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Varaamo
          </Typography>
        </Toolbar>
        <Tabs
          value={tab}
          onChange={(_, value) => setTab(value)}
          variant="scrollable"
          scrollButtons="auto"
        >
          <Tab label="Tilat" />
          <Tab label="Varaukset" />
          <Tab label="Uusi varaus" disabled={!canCreateReservation} />
        </Tabs>
      </AppBar>

      <Container sx={{ py: 3 }} maxWidth="md">
        {tab === 0 && (
          <RoomsList
            rooms={rooms}
            loading={roomsLoading}
            error={roomsError}
            onRefresh={() => void loadRooms()}
          />
        )}
        {tab === 1 && (
          <ReservationsList rooms={rooms} refreshToken={reservationsRefreshToken} />
        )}
        {tab === 2 && (
          <CreateReservationForm
            rooms={rooms}
            onCreated={() => {
              setReservationsRefreshToken((v) => v + 1)
              setTab(1)
            }}
          />
        )}
      </Container>
    </Box>
  )
}

export default App
