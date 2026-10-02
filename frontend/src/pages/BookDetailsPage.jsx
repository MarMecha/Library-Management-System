import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { apiRequest } from '../services/api'

function BookDetailsPage() {
  const { id } = useParams()
  const [book, setBook] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadBook() {
      try {
        const data = await apiRequest(`/books/${id}`)
        setBook(data)
      } catch (error) {
        setError(error.message)
      } finally {
        setIsLoading(false)
      }
    }

    loadBook()
  }, [id])

  if (isLoading) {
    return <p className="status-message">Loading book...</p>
  }

  if (error) {
    return (
      <p className="status-message error-message" role="alert">
        {error}
      </p>
    )
  }

  return (
    <section className="book-details">
      <Link to="/books">← Back to books</Link>

      <h2>{book.title}</h2>

      <dl>
        <div>
          <dt>ISBN</dt>
          <dd>{book.isbn}</dd>
        </div>

        <div>
          <dt>Publication year</dt>
          <dd>{book.publicationYear}</dd>
        </div>

        <div>
          <dt>Categories</dt>
          <dd>
            {book.categories?.length
              ? book.categories.map((category) => category.name).join(', ')
              : 'No categories'}
          </dd>
        </div>

        <div>
          <dt>Total copies</dt>
          <dd>{book.totalCopies}</dd>
        </div>

        <div>
          <dt>Authors</dt>
          <dd>
            {book.authors?.length
              ? book.authors
                  .map((author) => `${author.firstName} ${author.lastName}`)
                  .join(', ')
              : 'No authors'}
          </dd>
        </div>
      </dl>
    </section>
  )
}

export default BookDetailsPage
