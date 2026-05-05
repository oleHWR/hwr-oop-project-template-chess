package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class BoardTest {

    @Test
    fun `squareAt returns square for file and rank`() {
        // given
        val board = Board()

        // when
        val square = board.squareAt(File.H, 8)

        // then
        assertThat(square).isEqualTo(Square(File.H, 8))
    }

    @Test
    fun `pieceAt returns placed piece`() {
        // given
        val board = Board()
        val rook = Piece(PieceType.ROOK, Color.WHITE, Square(File.A, 1))

        // when
        board.place(rook)

        // then
        assertThat(board.pieceAt(Square(File.A, 1))).isEqualTo(rook)
    }

    @Test
    fun `place fails when square is already occupied`() {
        // given
        val board = Board()
        board.place(Piece(PieceType.KING, Color.WHITE, Square(File.E, 1)))

        // when / then
        assertThatThrownBy {
            board.place(Piece(PieceType.ROOK, Color.WHITE, Square(File.E, 1)))
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Square is already occupied")
    }

    @Test
    fun `pieceAt returns null when square has no piece`() {
        // given
        val board = Board()
        val square = Square(File.A, 1)

        // when
        val piece = board.pieceAt(square)

        // then
        assertThat(piece).isNull()
    }

    @Test
    fun `pieceAt returns piece on square`() {
        // given
        val board = Board()
        val king = Piece(PieceType.KING, Color.BLACK, Square(File.E, 8))

        // when
        board.place(king)
        val foundPiece = board.pieceAt(Square(File.E, 8))

        // then
        assertThat(foundPiece).isEqualTo(king)
    }

    @Test
    fun `applyMove moves piece to target square`() {
        // given
        val board = Board()
        val king = Piece(PieceType.KING, Color.WHITE, Square(File.E, 1))
        board.place(king)

        // when
        board.applyMove(Move(Square(File.E, 1), Square(File.E, 2)))

        // then
        val movedKing = board.pieceAt(Square(File.E, 2))
        assertThat(board.pieceAt(Square(File.E, 1))).isNull()
        assertThat(movedKing?.type).isEqualTo(PieceType.KING)
        assertThat(movedKing?.color).isEqualTo(Color.WHITE)
        assertThat(movedKing?.hasMoved).isTrue()
    }

    @Test
    fun `applyMove fails when no piece is on start square`() {
        // given
        val board = Board()

        // when / then
        assertThatThrownBy {
            board.applyMove(Move(Square(File.E, 1), Square(File.E, 2)))
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("No piece on start square")
    }

    @Test
    fun `applyMove fails when target square is occupied`() {
        // given
        val board = Board()
        board.place(Piece(PieceType.KING, Color.WHITE, Square(File.E, 1)))
        board.place(Piece(PieceType.ROOK, Color.WHITE, Square(File.E, 2)))

        // when / then
        assertThatThrownBy {
            board.applyMove(Move(Square(File.E, 1), Square(File.E, 2)))
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Target square is already occupied")
    }
}
