package com.marler.teammap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marler.teammap.common.Result;
import com.marler.teammap.dto.request.AddMatchEventRequest;
import com.marler.teammap.dto.response.MatchEventStatsVO;
import com.marler.teammap.pojo.MatchEvent;
import com.marler.teammap.service.MatchEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchEventController.class)
class MatchEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchEventService matchEventService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testAddMatchEvent() throws Exception {
        // given
        AddMatchEventRequest request = new AddMatchEventRequest();
        request.setMatchId(1);
        request.setSportType(1);
        request.setTeamId(10);
        request.setPlayerId(100);
        request.setType(1);
        request.setDescription("进球");
        request.setEventTime(30);

        MatchEvent expectedEvent = new MatchEvent();
        expectedEvent.setId(1L);
        expectedEvent.setMatchId(1);
        expectedEvent.setSportType(1);
        expectedEvent.setTeamId(10);
        expectedEvent.setPlayerId(100);
        expectedEvent.setType(1);
        expectedEvent.setDescription("进球");
        expectedEvent.setEventTime(30);
        expectedEvent.setCreateTime(LocalDateTime.now());

        when(matchEventService.add(any(AddMatchEventRequest.class))).thenReturn(expectedEvent);

        // when & then
        mockMvc.perform(post("/api/match-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.matchId").value(1))
                .andExpect(jsonPath("$.data.sportType").value(1))
                .andExpect(jsonPath("$.data.type").value(1))
                .andExpect(jsonPath("$.data.description").value("进球"));
    }

    @Test
    void testGetByMatchId() throws Exception {
        // given
        Integer matchId = 1;
        MatchEvent event1 = new MatchEvent();
        event1.setId(1L);
        event1.setMatchId(matchId);
        event1.setSportType(1);
        event1.setTeamId(10);
        event1.setPlayerId(100);
        event1.setType(1);
        event1.setDescription("进球");
        event1.setEventTime(30);

        MatchEvent event2 = new MatchEvent();
        event2.setId(2L);
        event2.setMatchId(matchId);
        event2.setSportType(1);
        event2.setTeamId(10);
        event2.setPlayerId(101);
        event2.setType(3);
        event2.setDescription("黄牌");
        event2.setEventTime(45);

        List<MatchEvent> events = Arrays.asList(event1, event2);
        when(matchEventService.getByMatchId(eq(matchId))).thenReturn(events);

        // when & then
        mockMvc.perform(get("/api/match-events/{matchId}", matchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].description").value("进球"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].description").value("黄牌"));
    }

    @Test
    void testGetByMatchId_empty() throws Exception {
        // given
        Integer matchId = 999;
        when(matchEventService.getByMatchId(eq(matchId))).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/match-events/{matchId}", matchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testGetEventStats() throws Exception {
        // given
        Integer matchId = 1;

        MatchEventStatsVO stats1 = new MatchEventStatsVO();
        stats1.setMatchId(1L);
        stats1.setSportType(1);
        stats1.setSportTypeName("足球");
        stats1.setTeamId(10);
        stats1.setTeamName("Team A");
        stats1.setTotalEvents(5);
        stats1.setFootballGoals(2);
        stats1.setYellowCards(1);
        stats1.setRedCards(0);

        MatchEventStatsVO stats2 = new MatchEventStatsVO();
        stats2.setMatchId(1L);
        stats2.setSportType(1);
        stats2.setSportTypeName("足球");
        stats2.setTeamId(11);
        stats2.setTeamName("Team B");
        stats2.setTotalEvents(3);
        stats2.setFootballGoals(1);
        stats2.setYellowCards(2);
        stats2.setRedCards(0);

        List<MatchEventStatsVO> statsList = Arrays.asList(stats1, stats2);
        when(matchEventService.getEventStatsByMatchId(eq(matchId))).thenReturn(statsList);

        // when & then
        mockMvc.perform(get("/api/match-events/{matchId}/stats", matchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].teamName").value("Team A"))
                .andExpect(jsonPath("$.data[0].totalEvents").value(5))
                .andExpect(jsonPath("$.data[0].footballGoals").value(2))
                .andExpect(jsonPath("$.data[1].teamName").value("Team B"))
                .andExpect(jsonPath("$.data[1].totalEvents").value(3));
    }

    @Test
    void testGetEventStats_empty() throws Exception {
        // given
        Integer matchId = 999;
        when(matchEventService.getEventStatsByMatchId(eq(matchId))).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/match-events/{matchId}/stats", matchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
