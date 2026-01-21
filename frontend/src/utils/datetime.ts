export function datetimeLocalToIso(value: string): string {
  // value from <input type="datetime-local">: "YYYY-MM-DDTHH:mm"
  // Interpret it in the user's local timezone and send UTC ISO.
  const date = new Date(value)
  return date.toISOString()
}

export function formatFinnishDateTime(value: string): string {
  const date = new Date(value)
  return date.toLocaleString('fi-FI', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}
