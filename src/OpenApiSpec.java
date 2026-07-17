/**
 * Holds the OpenAPI (Swagger) specification for the Library REST API as a
 * plain string, so it can be served by the built-in HttpServer without
 * pulling in any external dependency (no Maven/Spring available offline).
 */
public class OpenApiSpec {

    private OpenApiSpec() {
    }

    public static final String YAML = """
            openapi: 3.0.3
            info:
              title: Library REST API
              description: Simple REST API for the Library OOP project. Reads and writes data in Postgres (tables "books" and "library_members").
              version: "1.0.0"
            servers:
              - url: /
            tags:
              - name: Health
              - name: Books
              - name: Members
            paths:
              /api/health:
                get:
                  tags: [Health]
                  summary: Service health check
                  responses:
                    "200":
                      description: Service is up
                      content:
                        application/json:
                          schema:
                            type: object
                            properties:
                              status:
                                type: string
                                example: ok

              /api/books:
                get:
                  tags: [Books]
                  summary: List all books
                  responses:
                    "200":
                      description: Array of books
                      content:
                        application/json:
                          schema:
                            type: array
                            items:
                              $ref: "#/components/schemas/Book"
                post:
                  tags: [Books]
                  summary: Create a new book
                  requestBody:
                    required: true
                    content:
                      application/json:
                        schema:
                          $ref: "#/components/schemas/NewBook"
                        example:
                          bookId: B-04
                          title: Dune
                          author: Frank Herbert
                  responses:
                    "201":
                      description: Book created
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Book"
                    "400":
                      description: Missing required fields
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Error"
                    "409":
                      description: Book with this id already exists
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Error"

              /api/books/{id}:
                get:
                  tags: [Books]
                  summary: Get a single book by id
                  parameters:
                    - $ref: "#/components/parameters/BookId"
                  responses:
                    "200":
                      description: The book
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Book"
                    "404":
                      description: Book not found
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Error"
                delete:
                  tags: [Books]
                  summary: Delete a book
                  parameters:
                    - $ref: "#/components/parameters/BookId"
                  responses:
                    "200":
                      description: Book deleted
                      content:
                        application/json:
                          schema:
                            type: object
                            properties:
                              message:
                                type: string
                    "404":
                      description: Book not found
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Error"

              /api/books/{id}/borrow:
                put:
                  tags: [Books]
                  summary: Mark a book as borrowed
                  parameters:
                    - $ref: "#/components/parameters/BookId"
                  responses:
                    "200":
                      description: Book borrowed
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Book"
                    "404":
                      description: Book not found
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Error"
                    "409":
                      description: Book is already borrowed
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Error"

              /api/books/{id}/return:
                put:
                  tags: [Books]
                  summary: Mark a book as returned / available
                  parameters:
                    - $ref: "#/components/parameters/BookId"
                  responses:
                    "200":
                      description: Book returned
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Book"
                    "404":
                      description: Book not found
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Error"
                    "409":
                      description: Book is already available
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Error"

              /api/members:
                get:
                  tags: [Members]
                  summary: List all members
                  responses:
                    "200":
                      description: Array of members
                      content:
                        application/json:
                          schema:
                            type: array
                            items:
                              $ref: "#/components/schemas/Member"
                post:
                  tags: [Members]
                  summary: Register a new member
                  requestBody:
                    required: true
                    content:
                      application/json:
                        schema:
                          $ref: "#/components/schemas/Member"
                        example:
                          userId: U-01
                          name: Alisher
                  responses:
                    "201":
                      description: Member created
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Member"
                    "400":
                      description: Missing required fields
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Error"
                    "409":
                      description: Member with this id already exists
                      content:
                        application/json:
                          schema:
                            $ref: "#/components/schemas/Error"

            components:
              parameters:
                BookId:
                  name: id
                  in: path
                  required: true
                  schema:
                    type: string
                  example: B-01
              schemas:
                Book:
                  type: object
                  properties:
                    bookId:
                      type: string
                      example: B-01
                    title:
                      type: string
                      example: Grokking Algorithms
                    author:
                      type: string
                      example: Aditya Bhargava
                    available:
                      type: boolean
                      example: true
                NewBook:
                  type: object
                  required: [bookId, title, author]
                  properties:
                    bookId:
                      type: string
                    title:
                      type: string
                    author:
                      type: string
                Member:
                  type: object
                  required: [userId, name]
                  properties:
                    userId:
                      type: string
                      example: U-01
                    name:
                      type: string
                      example: Alisher
                Error:
                  type: object
                  properties:
                    error:
                      type: string
            """;

    public static final String DOCS_HTML = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <title>Library REST API — Docs</title>
              <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5/swagger-ui.css">
            </head>
            <body>
              <div id="swagger-ui"></div>
              <script src="https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js"></script>
              <script>
                window.onload = () => {
                  window.ui = SwaggerUIBundle({
                    url: "/api/openapi.yaml",
                    dom_id: "#swagger-ui",
                    presets: [SwaggerUIBundle.presets.apis],
                    layout: "BaseLayout"
                  });
                };
              </script>
            </body>
            </html>
            """;
}