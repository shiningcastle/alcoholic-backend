package someone.alcoholic.service.board_image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import someone.alcoholic.repository.board_image.BoardImageRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardImageServiceImpl implements BoardImageService {

    private final BoardImageRepository boardImageRepository;

}
