import { useEffect, useState } from 'react'
import { apiRequest } from '../services/api'
import { Link } from 'react-router-dom'

function BooksPage() {
  const [books, setBooks] = useState([])
  const [page, setPage] = useState(0)
  const [pageInfo, setPageInfo] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')
  const [searchTerm, setSearchTerm] = useState('')
  const [searchQuery, setSearchQuery] = useState('')

  useEffect(() => {
    async function loadBooks() {
      setIsLoading(true)
      setError('')

      try {
        if (searchQuery) {
          const data = await apiRequest(
            `/books/search?title=${encodeURIComponent(searchQuery)}`,
          )

          setBooks(data)
          setPageInfo(null)
        } else {
          const data = await apiRequest(
            `/books?page=${page}&size=2&sort=title,asc`,
          )

          setBooks(data.content)
          setPageInfo(data)
        }
      } catch (error) {
        setError(error.message)
      } finally {
        setIsLoading(false)
      }
    }


    loadBooks()
  }, [page, searchQuery])
  function handleSearch(event) {
    event.preventDefault()
    setPage(0)
    setSearchQuery(searchTerm.trim())
  }

  function clearSearch() {
    setSearchTerm('')
    setSearchQuery('')
    setPage(0)
  }

  if (isLoading) {
    return <p className="status-message">Loading books...</p>
  }

  if (error) {
    return (
      <p className="status-message error-message" role="alert">
        {error}
      </p>
    )
  }

  return (
    <section className="books-section">
      <div className="section-heading">
        <div>
          <h2>Books</h2>
          <p>Browse the books available in the library.</p>
        </div>
        <form className="search-form" onSubmit={handleSearch}>
          <input
            type="search"
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
            placeholder="Search by title"
            aria-label="Search books by title"
          />

          <button type="submit">Search</button>

          {searchQuery && (
            <button type="button" className="clear-button" onClick={clearSearch}>
              Clear
            </button>
          )}
        </form>
      </div>

      {books.length === 0 ? (
        <p>No books were found.</p>
      ) : (
        <>
          <div className="table-container">
            <table className="books-table">
              <thead>
                <tr>
                  <th>Title</th>
                  <th>ISBN</th>
                  <th>Year</th>
                  <th>Category</th>
                  <th>Copies</th>
                </tr>
              </thead>

              <tbody>
                {books.map((book) => (
                  <tr key={book.id}>
                    <td>
                      <Link className="book-link" to={`/books/${book.id}`}>
                        {book.title}
                      </Link>
                    </td>
                    <td>{book.isbn}</td>
                    <td>{book.publicationYear}</td>
                    <td>
                      {book.categories?.length
                        ? book.categories.map((category) => category.name).join(', ')
                        : 'No categories'}
                    </td>
                    <td>{book.totalCopies}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {pageInfo && (
            <div className="pagination">
              <button
                type="button"
                disabled={pageInfo.first}
                onClick={() => setPage((currentPage) => currentPage - 1)}
              >
                Previous
              </button>

              <span>
                Page {pageInfo.page + 1} of {pageInfo.totalPages}
              </span>

              <button
                type="button"
                disabled={pageInfo.last}
                onClick={() => setPage((currentPage) => currentPage + 1)}
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </section>
  )
}

export default BooksPage
