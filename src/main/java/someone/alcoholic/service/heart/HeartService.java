package someone.alcoholic.service.heart;

import java.security.Principal;

public interface HeartService {
    void saveBoardHeart(Principal principal, Long boardSeq);
    void deleteBoardHeart(Principal principal, Long boardSeq);
}
