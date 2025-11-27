import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  
  // CRITICAL FIX: Ensure all route requests fall back to index.html for React Router DOM
  // Vite's dev server already serves index.html for client-side routing in development;
  // remove the unsupported 'historyApiFallback' option. If you need a custom fallback,
  // add a middleware via configureServer and connect-history-api-fallback.
  server: {
    // custom server options can go here
  },
  
  // NOTE: If you deploy to a subdirectory, you would also need:
  // base: '/my-faculty-portal/' 
});