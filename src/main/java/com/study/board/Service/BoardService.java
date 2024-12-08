package com.study.board.Service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;
    public Model write(Board board, Model model, String massage, String searchURL) throws Exception{

        //String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        //UUID uuid = UUID.randomUUID();

        //String filename = uuid+"_"+file.getOriginalFilename();

        //File saveFile = new File(projectPath, filename);

        //file.transferTo(saveFile);

        //board.setFilename(filename);

        //board.setFilepath("/files/"+filename);

        boardRepository.save(board);

        //model.addAttribute("message", massage);
        //model.addAttribute("searchUrl", searchURL);

        model = massageHandler(model, massage, searchURL);

        return model;
    }

    /*public Page<Board> boardList(Pageable pageable){

        return boardRepository.findAll(pageable);
    }*/

    public Page<Board> boardList(String searchKeyword, Pageable pageable){

        if(searchKeyword == null) return boardRepository.findAll(pageable);
        else return boardRepository.findByTitleContaining(searchKeyword, pageable);

    }

    public Model boardListModel(Model model, Pageable pageable, String searchKeyword){

        Page<Board> list = null;

        list = boardList(searchKeyword, pageable);


        int nowPage = list.getPageable().getPageNumber()+1;
        int startPage = Math.max(nowPage-4, 1);
        int endPage = Math.min(nowPage+5, list.getTotalPages());

        if(startPage > endPage) endPage = startPage;

        model.addAttribute("list", list);
        model.addAttribute("nowPage" , nowPage);
        model.addAttribute("startPage" , startPage);
        model.addAttribute("endPage" , endPage);

        return model;
    }

    public Board boardView(Integer id){

        return boardRepository.findById(id).get();
    }

    public Model boardDelete(Model model, Integer id){

        boardRepository.deleteById(id);

        //model.addAttribute("message", "글 삭제가 완료되었습니다.");
        //model.addAttribute("searchUrl", "/board/list");
        model = massageHandler(model, "글 삭제가 완료되었습니다.", "/board/list");


        return model;
    }

    public Model boardViewModel(Model model, Integer id) {

        model.addAttribute("board", boardView(id));

        return model;
    }

    public Model massageHandler(Model model, String massage, String searchURL){

        model.addAttribute("message", massage);
        model.addAttribute("searchUrl", searchURL);


        return model;
    }

    public Model modify(Integer id, Model model, Board board) throws Exception {

        Board boardTemp = boardView(id);

        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        model = write(boardTemp, model, "글 수정이 완료되었습니다.", "/board/list");

        return model;
    }
}
