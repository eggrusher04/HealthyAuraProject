export default function Layout({ children }) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 to-emerald-100 text-gray-800">
      <main className="max-w-4xl mx-auto px-4 py-8">{children}</main>
    </div>
  );
}
