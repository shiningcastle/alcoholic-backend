package someone.alcoholic.service.heart;

import javax.servlet.http.HttpServletRequest;

public interface HeartService {
    void saveBoardHeart(HttpServletRequest request, Long boardSeq);
    void deleteBoardHeart(HttpServletRequest request, Long boardSeq);
}
