/**
* Daniel Ricci <thedanny09@icloud.com>
*
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without restriction,
* including without limitation the rights to use, copy, modify, merge,
* publish, distribute, sublicense, and/or sell copies of the Software,
* and to permit persons to whom the Software is furnished to do so, subject
* to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
* THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
* IN THE SOFTWARE.
*/

package game.controllers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JPanel;

import game.GameInstance;
import game.models.PlayerModel;
import game.models.PlayerModel.Team;
import game.views.BoardGameView;
import game.views.BoardGameView.BoardPosition;

public class BoardGameController {

	private BoardGameView _view = null;
	private boolean _isGameOver = false;
	private int _gridSize = 0;
	
	private final ArrayList<PlayerModel> _players = new ArrayList<PlayerModel>();	
	private final Queue<PlayerModel> _turns = new LinkedList<PlayerModel>();
			
	private BoardGameController() {
		registerController();
		addView(new BoardGameView());
		
		_players.add(new PlayerModel(Team.PlayerX));
		_players.add(new PlayerModel(Team.PlayerY));
		
		for(PlayerModel player : _players) {
			_turns.add(player);
		}

		_gridSize = 3;	
	}
	
	private void loadPlayers() {
		_players.clear();
		_turns.clear();
		
		_players.add(new PlayerModel(Team.PlayerX));
		_players.add(new PlayerModel(Team.PlayerY));
		
		for(PlayerModel player : _players) {
			_turns.add(player);
		}
	}
	
	public BoardGameController(JPanel source) {
		this();
		source.add(_view);
	}
	
	private void registerController() {
		GameInstance.getInstance().registerController(this);
	}
	
	public boolean isWinningPosition(BoardPosition position, List<BoardPosition> positions) {
		
		boolean result = 
			isWinningRow(position, positions) || 
			isWinningColumn(position, positions) || 
			isWinningDiagonal(position, positions);
		
		positions.add(position);
		
		return result;
	}
	
	public boolean isGameOver() { return _isGameOver; }
	
	private boolean isWinningRow(BoardPosition position, List<BoardPosition> positions) {
		
		positions.clear();
		int rowSize = _gridSize - 1;
		
		BoardPosition temp = position;
		while((temp = temp.getNeighbourLeft()) != null && temp.equals(position))
		{
			positions.add(temp);
			--rowSize;
		}
		
		temp = position;
		while((temp = temp.getNeighbourRight()) != null && temp.equals(position))
		{
			positions.add(temp);
			--rowSize;
		}
		
		assert rowSize > -1 : "Winning position invalid, something went wrong with the grid size";
		return rowSize == 0;
	}
	
	private boolean isWinningColumn(BoardPosition position, List<BoardPosition> positions) {
		
		positions.clear();
		int colSize = _gridSize - 1;
		
		BoardPosition temp = position;
		while((temp = temp.getNeighbourTop()) != null && temp.equals(position))
		{
			positions.add(temp);
			--colSize;
		}
		
		temp = position;
		while((temp = temp.getNeighbourBottom()) != null && temp.equals(position))
		{
			positions.add(temp);
			--colSize;
		}
		
		assert colSize > -1 : "Winning position invalid, something went wrong with the grid size";
		return colSize == 0;
	}
	
	private boolean isWinningDiagonal(BoardPosition position, List<BoardPosition> positions) {
		
		positions.clear();
		int colSize = _gridSize - 1;
		BoardPosition temp = position;
		
		while((temp = temp.getNeighbourTop()) != null && (temp = temp.getNeighbourRight()) != null && temp.equals(position))
		{
			positions.add(temp);
			--colSize;
		}
		
		temp = position;
		while((temp = temp.getNeighbourBottom()) != null && (temp = temp.getNeighbourLeft()) != null && temp.equals(position))
		{
			positions.add(temp);	
			--colSize;
		}
		
		if(colSize > 0)
		{
			positions.clear();
			colSize = _gridSize - 1;
			temp = position;
			
			while((temp = temp.getNeighbourTop()) != null && (temp = temp.getNeighbourLeft()) != null && temp.equals(position))
			{
				positions.add(temp);
				--colSize;
			}
			
			temp = position;
			while((temp = temp.getNeighbourBottom()) != null && (temp = temp.getNeighbourRight()) != null && temp.equals(position))
			{
				positions.add(temp);	
				--colSize;
			}
		}
		
	
		return colSize == 0;
	}
	
	
	public String getPlayerToken() { return _turns.peek().getTokenPath(); }
	public PlayerModel getCurrentPlayer() { return _turns.peek(); }
	
	private void nextPlayer() {
		_turns.add(_turns.poll());
	}
	
	public void execute() {	
		_view.render();		
	}
		
	public int getGridSize() { return _gridSize; }
	
	private void addView(BoardGameView view) {
		if(_view == null) {
			_view = view;
			_view.addController(this);
		}
	}

	public void performMove(java.awt.event.InputEvent event) {

		List<BoardPosition> winningPositions = new ArrayList<BoardPosition>();
		
		if(!(_isGameOver || isWinningPosition((BoardPosition)event.getSource(), winningPositions))) {
			if(!_view.movesLeft()) {
				_isGameOver = true;
				_view.highlightAll();
			} else {
				nextPlayer();	
			}
		} else {

			_isGameOver = true;
							
			for(BoardPosition position : winningPositions)
			{
				position.setBackground(Color.RED);
			}
		}	
	}

	public void reload() {
		loadPlayers();
		_isGameOver = false;
		_view.reload();
	}
}