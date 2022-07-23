package someone.alcoholic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import someone.alcoholic.domain.nickname.Nickname;

@Repository
public interface NicknameRepository extends JpaRepository<Nickname, Long> {
    @Query(value = "select r1.adjective\n" +
            "from nickname as r1 join\n" +
                "(select ceil(rand() *\n" +
                    "(select max(seq)\n" +
                        "from nickname)) as seq)\n" +
                    "as r2\n" +
            "where r1.seq >= r2.seq\n" +
            "order by r1.seq asc\n" +
            "limit 1", nativeQuery = true)
    String findRandomAdj();

    @Query(value = "select r1.noun\n" +
            "from nickname as r1 join\n" +
            "(select ceil(rand() *\n" +
            "(select max(seq)\n" +
            "from nickname)) as seq)\n" +
            "as r2\n" +
            "where r1.seq >= r2.seq\n" +
            "order by r1.seq asc\n" +
            "limit 1", nativeQuery = true)
    String findRandomNoun();
}
