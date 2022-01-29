package nextstep.subway.line.service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {

    @Mock
    LineRepository lineRepository;

    @Mock
    StationRepository stationRepository;

    @InjectMocks
    LineService lineService;

    long 이호선;
    long 이호선_상행종점역;
    long 이호선_하행종점역;

    @BeforeEach
    void setUp() {
        Line line = Line.builder()
                .id(1L)
                .name("2호선")
                .color("bg-green")
                .build();

        Station 신도림역 = new Station(1L, "신도림역");
        Station 영등포구청역 = new Station(2L, "영등포구청역");

        Section section = Section.builder()
                .upStation(신도림역)
                .downStation(영등포구청역)
                .line(line)
                .distance(6)
                .build();
        ReflectionTestUtils.setField(section, "id", 1L);
        line.addSection(section);

        Mockito.when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        Mockito.when(stationRepository.findById(신도림역.getId())).thenReturn(Optional.of(신도림역));
        Mockito.when(stationRepository.findById(영등포구청역.getId())).thenReturn(Optional.of(영등포구청역));

        이호선 = line.getId();
        이호선_상행종점역 = 신도림역.getId();
        이호선_하행종점역 = 영등포구청역.getId();
    }

    @Test
    void 구간_추가() {
        // given
        Station station = new Station(3L, "문래역");
        long 문래역 = station.getId();
        Mockito.when(stationRepository.findById(문래역)).thenReturn(Optional.of(station));

        SectionRequest request = new SectionRequest(이호선_상행종점역, 문래역, 7);

        // when
        lineService.addSection(이호선, request);
        LineResponse line = lineService.findLine(이호선);

        // then
        List<Long> lineIds = line.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(lineIds).containsExactly(이호선_상행종점역, 문래역, 이호선_하행종점역);
    }
}
