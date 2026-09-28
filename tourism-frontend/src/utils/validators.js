export function isBlank(value) {
  return value === undefined || value === null || String(value).trim() === ''
}

export function isValidEmail(value) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value || '')
}

export function validateRegisterForm(form) {
  const errors = {}
  if (isBlank(form.fullName)) errors.fullName = 'Full name is required.'
  if (isBlank(form.username) || form.username.length < 3) errors.username = 'Username must be at least 3 characters.'
  if (!isValidEmail(form.email)) errors.email = 'Enter a valid email address.'
  if (isBlank(form.password) || form.password.length < 6) errors.password = 'Password must be at least 6 characters.'
  return errors
}

export function validateLoginForm(form) {
  const errors = {}
  if (isBlank(form.username)) errors.username = 'Username is required.'
  if (isBlank(form.password)) errors.password = 'Password is required.'
  return errors
}
