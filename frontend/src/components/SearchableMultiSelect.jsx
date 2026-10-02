import { useEffect, useRef, useState } from 'react'

function SearchableMultiSelect({
  id,
  label,
  placeholder,
  options,
  selectedIds,
  onAdd,
  onRemove,
  getOptionLabel,
  disabled = false,
}) {
  const [isOpen, setIsOpen] = useState(false)
  const [search, setSearch] = useState('')
  const containerRef = useRef(null)
  const inputRef = useRef(null)

  const availableOptions = options.filter(
    (option) =>
      !selectedIds.includes(option.id) &&
      getOptionLabel(option)
        .toLowerCase()
        .includes(search.trim().toLowerCase()),
  )

  useEffect(() => {
    function handleOutsideClick(event) {
      if (!containerRef.current?.contains(event.target)) {
        setIsOpen(false)
        setSearch('')
      }
    }

    document.addEventListener('mousedown', handleOutsideClick)

    return () => document.removeEventListener('mousedown', handleOutsideClick)
  }, [])

  function openDropdown() {
    if (disabled) {
      return
    }

    setIsOpen(true)
    requestAnimationFrame(() => inputRef.current?.focus())
  }

  function handleKeyDown(event) {
    if (event.key === 'Escape') {
      setIsOpen(false)
      setSearch('')
    }
  }

  function selectOption(optionId) {
    onAdd(optionId)
    setSearch('')
    inputRef.current?.focus()
  }

  return (
    <div className="multi-select-field" ref={containerRef}>
      <label id={`${id}-label`} htmlFor={isOpen ? `${id}-search` : id}>
        {label}
      </label>

      <div className="searchable-select">
        {isOpen ? (
          <input
            ref={inputRef}
            id={`${id}-search`}
            type="search"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            onKeyDown={handleKeyDown}
            placeholder={`Search ${label.toLowerCase()}`}
            aria-labelledby={`${id}-label`}
            aria-expanded="true"
            aria-controls={`${id}-options`}
          />
        ) : (
          <button
            id={id}
            type="button"
            className="searchable-select-trigger"
            onClick={openDropdown}
            disabled={disabled}
            aria-labelledby={`${id}-label ${id}`}
            aria-expanded="false"
          >
            <span>{placeholder}</span>
            <span aria-hidden="true">⌄</span>
          </button>
        )}

        {isOpen && (
          <div className="searchable-select-menu" id={`${id}-options`}>
            {availableOptions.length > 0 ? (
              availableOptions.map((option) => (
                <button
                  type="button"
                  className="searchable-select-option"
                  key={option.id}
                  onClick={() => selectOption(option.id)}
                >
                  {getOptionLabel(option)}
                </button>
              ))
            ) : (
              <p className="searchable-select-empty">No matching options.</p>
            )}
          </div>
        )}
      </div>

      <div className="selection-chips" aria-label={`Selected ${label}`}>
        {selectedIds.map((optionId) => {
          const option = options.find((item) => item.id === optionId)

          return (
            <span className="selection-chip" key={optionId}>
              {option && getOptionLabel(option)}
              <button
                type="button"
                aria-label={`Remove ${option ? getOptionLabel(option) : label}`}
                onClick={() => onRemove(optionId)}
              >
                ×
              </button>
            </span>
          )
        })}
      </div>
    </div>
  )
}

export default SearchableMultiSelect
