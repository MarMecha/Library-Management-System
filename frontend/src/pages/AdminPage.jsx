import { useEffect, useState } from 'react'
import { apiRequest } from '../services/api'
import SearchableMultiSelect from '../components/SearchableMultiSelect'

function AdminPage() {
  const [categories, setCategories] = useState([])
  const [authors, setAuthors] = useState([])
  const [books, setBooks] = useState([])
  const [categoryName, setCategoryName] = useState('')
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [title, setTitle] = useState('')
  const [isbn, setIsbn] = useState('')
  const [publicationYear, setPublicationYear] = useState('')
  const [totalCopies, setTotalCopies] = useState('')
  const [selectedCategoryIds, setSelectedCategoryIds] = useState([])
  const [selectedAuthorIds, setSelectedAuthorIds] = useState([])
  const [editingBookId, setEditingBookId] = useState(null)
  const [categoryMessage, setCategoryMessage] = useState('')
  const [authorMessage, setAuthorMessage] = useState('')
  const [bookMessage, setBookMessage] = useState('')
  const [error, setError] = useState('')
  const [isLoadingOptions, setIsLoadingOptions] = useState(true)

  async function loadOptions() {
    setIsLoadingOptions(true)
    setError('')

    try {
      const [categoryData, authorData, bookData] = await Promise.all([
        apiRequest('/categories?page=0&size=100&sort=name,asc'),
        apiRequest('/authors?page=0&size=100&sort=lastName,asc'),
        apiRequest('/books?page=0&size=100&sort=title,asc'),
      ])

      setCategories(categoryData.content)
      setAuthors(authorData.content)
      setBooks(bookData.content)
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setIsLoadingOptions(false)
    }
  }

  useEffect(() => {
    let isActive = true

    async function fetchInitialOptions() {
      try {
        const [categoryData, authorData, bookData] = await Promise.all([
          apiRequest('/categories?page=0&size=100&sort=name,asc'),
          apiRequest('/authors?page=0&size=100&sort=lastName,asc'),
          apiRequest('/books?page=0&size=100&sort=title,asc'),
        ])

        if (isActive) {
          setCategories(categoryData.content)
          setAuthors(authorData.content)
          setBooks(bookData.content)
        }
      } catch (requestError) {
        if (isActive) {
          setError(requestError.message)
        }
      } finally {
        if (isActive) {
          setIsLoadingOptions(false)
        }
      }
    }

    fetchInitialOptions()

    return () => {
      isActive = false
    }
  }, [])

  function addCategory(id) {
    if (id && !selectedCategoryIds.includes(id)) {
      setSelectedCategoryIds((currentIds) => [...currentIds, id])
    }
  }

  function removeCategory(id) {
    setSelectedCategoryIds((currentIds) =>
      currentIds.filter((categoryId) => categoryId !== id),
    )
  }

  function addAuthor(id) {
    if (id && !selectedAuthorIds.includes(id)) {
      setSelectedAuthorIds((currentIds) => [...currentIds, id])
    }
  }

  function removeAuthor(id) {
    setSelectedAuthorIds((currentIds) =>
      currentIds.filter((authorId) => authorId !== id),
    )
  }

  async function handleCategorySubmit(event) {
    event.preventDefault()
    setCategoryMessage('')
    setError('')

    try {
      const category = await apiRequest('/categories', {
        method: 'POST',
        body: JSON.stringify({ name: categoryName }),
      })

      setCategoryName('')
      setCategoryMessage(`Category "${category.name}" created.`)
      await loadOptions()
    } catch (requestError) {
      setError(requestError.message)
    }
  }

  async function handleAuthorSubmit(event) {
    event.preventDefault()
    setAuthorMessage('')
    setError('')

    try {
      const author = await apiRequest('/authors', {
        method: 'POST',
        body: JSON.stringify({ firstName, lastName }),
      })

      setFirstName('')
      setLastName('')
      setAuthorMessage(
        `Author "${author.firstName} ${author.lastName}" created.`,
      )
      await loadOptions()
    } catch (requestError) {
      setError(requestError.message)
    }
  }

  function resetBookForm() {
    setTitle('')
    setIsbn('')
    setPublicationYear('')
    setTotalCopies('')
    setSelectedCategoryIds([])
    setSelectedAuthorIds([])
    setEditingBookId(null)
  }

  function startEditingBook(book) {
    setTitle(book.title)
    setIsbn(book.isbn)
    setPublicationYear(String(book.publicationYear))
    setTotalCopies(String(book.totalCopies))
    setSelectedCategoryIds(book.categories.map((category) => category.id))
    setSelectedAuthorIds(book.authors.map((author) => author.id))
    setEditingBookId(book.id)
    setBookMessage('')
    setError('')

    document.getElementById('book-form')?.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
    })
  }

  async function handleDeleteBook(book) {
    const shouldDelete = window.confirm(
      `Delete "${book.title}"? This action cannot be undone.`,
    )

    if (!shouldDelete) {
      return
    }

    setBookMessage('')
    setError('')

    try {
      await apiRequest(`/books/${book.id}`, { method: 'DELETE' })

      if (editingBookId === book.id) {
        resetBookForm()
      }

      setBookMessage(`Book "${book.title}" deleted.`)
      await loadOptions()
    } catch (requestError) {
      setError(requestError.message)
    }
  }

  async function handleBookSubmit(event) {
    event.preventDefault()
    setBookMessage('')
    setError('')

    try {
      const book = await apiRequest(
        editingBookId ? `/books/${editingBookId}` : '/books',
        {
        method: editingBookId ? 'PUT' : 'POST',
        body: JSON.stringify({
          title,
          isbn,
          publicationYear: Number(publicationYear),
          totalCopies: Number(totalCopies),
          categoryIds: selectedCategoryIds,
          authorIds: selectedAuthorIds,
        }),
        },
      )

      const action = editingBookId ? 'updated' : 'created'

      resetBookForm()
      setBookMessage(`Book "${book.title}" ${action}.`)
      await loadOptions()
    } catch (requestError) {
      setError(requestError.message)
    }
  }

  return (
    <section className="admin-page">
      <div className="admin-heading">
        <h2>Admin dashboard</h2>
        <p>Create catalog resources without using Swagger.</p>
      </div>

      {error && (
        <p className="form-error admin-error" role="alert">
          {error}
        </p>
      )}

      <div className="admin-grid">
        <form className="admin-card admin-form" onSubmit={handleCategorySubmit}>
          <h3>Create category</h3>
          <label htmlFor="categoryName">Name</label>
          <input
            id="categoryName"
            value={categoryName}
            onChange={(event) => setCategoryName(event.target.value)}
            required
          />
          <button type="submit">Create category</button>
          {categoryMessage && <p className="success-message">{categoryMessage}</p>}
        </form>

        <form className="admin-card admin-form" onSubmit={handleAuthorSubmit}>
          <h3>Create author</h3>
          <label htmlFor="firstName">First name</label>
          <input
            id="firstName"
            value={firstName}
            onChange={(event) => setFirstName(event.target.value)}
            required
          />
          <label htmlFor="lastName">Last name</label>
          <input
            id="lastName"
            value={lastName}
            onChange={(event) => setLastName(event.target.value)}
            required
          />
          <button type="submit">Create author</button>
          {authorMessage && <p className="success-message">{authorMessage}</p>}
        </form>

        <form
          id="book-form"
          className="admin-card admin-form book-form"
          onSubmit={handleBookSubmit}
        >
          <h3>{editingBookId ? 'Edit book' : 'Create book'}</h3>
          <label htmlFor="title">Title</label>
          <input
            id="title"
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            required
          />
          <label htmlFor="isbn">ISBN</label>
          <input
            id="isbn"
            value={isbn}
            onChange={(event) => setIsbn(event.target.value)}
            required
          />
          <label htmlFor="publicationYear">Publication year</label>
          <input
            id="publicationYear"
            type="number"
            min="1"
            value={publicationYear}
            onChange={(event) => setPublicationYear(event.target.value)}
            required
          />
          <label htmlFor="totalCopies">Total copies</label>
          <input
            id="totalCopies"
            type="number"
            min="1"
            value={totalCopies}
            onChange={(event) => setTotalCopies(event.target.value)}
            required
          />
          <SearchableMultiSelect
            id="categoryId"
            label="Categories"
            placeholder="Add category"
            options={categories}
            selectedIds={selectedCategoryIds}
            onAdd={addCategory}
            onRemove={removeCategory}
            getOptionLabel={(category) => category.name}
            disabled={isLoadingOptions}
          />

          <SearchableMultiSelect
            id="authorId"
            label="Authors"
            placeholder="Add author"
            options={authors}
            selectedIds={selectedAuthorIds}
            onAdd={addAuthor}
            onRemove={removeAuthor}
            getOptionLabel={(author) =>
              `${author.firstName} ${author.lastName}`
            }
            disabled={isLoadingOptions}
          />

          <button
            type="submit"
            disabled={
              isLoadingOptions ||
              selectedCategoryIds.length === 0 ||
              selectedAuthorIds.length === 0
            }
          >
            {editingBookId ? 'Update book' : 'Create book'}
          </button>
          {editingBookId && (
            <button
              type="button"
              className="cancel-edit-button"
              onClick={resetBookForm}
            >
              Cancel edit
            </button>
          )}
          {bookMessage && <p className="success-message">{bookMessage}</p>}
        </form>
      </div>

      <section className="admin-books">
        <div className="admin-books-heading">
          <div>
            <h3>Manage books</h3>
            <p>Edit or delete existing catalog entries.</p>
          </div>
          <span>{books.length} books</span>
        </div>

        {books.length === 0 ? (
          <p>No books were found.</p>
        ) : (
          <div className="table-container">
            <table className="books-table">
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Categories</th>
                  <th>Authors</th>
                  <th>Copies</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {books.map((book) => (
                  <tr key={book.id}>
                    <td>{book.title}</td>
                    <td>
                      {book.categories
                        .map((category) => category.name)
                        .join(', ')}
                    </td>
                    <td>
                      {book.authors
                        .map(
                          (author) =>
                            `${author.firstName} ${author.lastName}`,
                        )
                        .join(', ')}
                    </td>
                    <td>{book.totalCopies}</td>
                    <td>
                      <div className="table-actions">
                        <button
                          type="button"
                          className="edit-button"
                          onClick={() => startEditingBook(book)}
                        >
                          Edit
                        </button>
                        <button
                          type="button"
                          className="delete-button"
                          onClick={() => handleDeleteBook(book)}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </section>
  )
}

export default AdminPage
