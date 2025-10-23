import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'fs'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // base: '~s367452/dist',
  server: {
    https: {
      key: fs.readFileSync('certificate/key.pem'),
      cert: fs.readFileSync('certificate/cert.pem')
    }
  },
})
