package com.ssafy.health.domain.crew.controller;

import com.ssafy.health.common.ApiResponse;
import com.ssafy.health.domain.account.dto.response.ValidateNameSuccessDto;
import com.ssafy.health.domain.battle.dto.response.BattleMatchResponseDto;
import com.ssafy.health.domain.crew.dto.request.CreateCrewRequestDto;
import com.ssafy.health.domain.crew.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "크루 컨트롤러", description = "크루 생성, 조회, 삭제 등 크루를 관리하는 클래스")
public interface CrewControllerApi {
    @Operation(
            summary = "크루 생성",
            description = "크루를 생성합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "크루 생성 완료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"크루가 생성되었습니다.\",\n" +
                                    "  \"data\": {\n" +
                                    "  }\n" +
                                    "}"
                            ))
            ),
    })
    ApiResponse<CreateCrewSuccessDto> createCrew(
            @RequestPart("data") CreateCrewRequestDto createCrewRequestDto,
            @RequestPart("profileImage") MultipartFile profileImage) throws IOException;

    @Operation(
            summary = "크루 상세 조회",
            description = "크루 아이디를 통해 크루 상세를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "크루 상세 조회 완료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"crewId\": 1,\n" +
                                    "    \"crewName\": \"달리는 번개\",\n" +
                                    "    \"exerciseName\": \"러닝\",\n" +
                                    "    \"profileImage\": \"crew-profile-image.jpg\",\n" +
                                    "    \"description\": \"번개맨보다 빠른 러너들의 모임\",\n" +
                                    "    \"crewCoins\": 300,\n" +
                                    "    \"crewRanking\": 3,\n" +
                                    "    \"totalBattleCount\": 10,\n" +
                                    "    \"winCount\": 7,\n" +
                                    "    \"averageAge\": 29,\n" +
                                    "    \"activityScore\": 1200,\n" +
                                    "    \"basicScore\": 900,\n" +
                                    "    \"role\": \"LEADER\"\n" +
                                    "  }\n" +
                                    "}"
                            ))
            ),
    })
    ApiResponse<CrewDetailResponseDto> getCrewDetail(@PathVariable("crew_id") Long crewId);

    @Operation(
            summary = "가입된 크루 조회",
            description = "회원의 가입된 크루 조회"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "가입된 크루 조회 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"crewList\": [\n" +
                                    "      {\n" +
                                    "        \"crewId\": 2,\n" +
                                    "        \"crewName\": \"달리자\",\n" +
                                    "        \"exerciseName\": \"러닝\",\n" +
                                    "        \"crewProfileImage\": \"crew-profile-image.jpg\",\n" +
                                    "        \"basicScore\": 0.0, \n"+
                                    "        \"activityScore\": 0.0 \n"+
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"crewId\": 3,\n" +
                                    "        \"crewName\": \"달리자\",\n" +
                                    "        \"exerciseName\": \"러닝\",\n" +
                                    "        \"crewProfileImage\": \"crew-profile-image.jpg\",\n" +
                                    "        \"basicScore\": 0.0, \n"+
                                    "        \"activityScore\": 0.0 \n"+
                                    "      }\n" +
                                    "    ]\n" +
                                    "  }\n" +
                                    "}"
                            )
                    )
            )
    })
    ApiResponse<CrewListResponseDto> getJoinedCrewList(@PathVariable("user_id") Long userId);

    @Operation(
            summary = "크루 가입",
            description = "크루에 가입합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "크루 가입 완료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"크루에 가입되었습니다.\",\n" +
                                    "  \"data\": {\n" +
                                    "  }\n" +
                                    "}"
                            ))
            ),
    })
    ApiResponse<JoinCrewSuccessDto> joinCrew(@PathVariable("crew_id") Long crewId);

    @Operation(
            summary = "코인 전송",
            description = "크루에 코인을 전송합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "코인 전송 완료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"message\": \"코인을 전송하였습니다.\",\n" +
                                    "    \"crewCoin\": 1000,\n" +
                                    "    \"myCoin\": 900\n" +
                                    "  }\n" +
                                    "}"
                            ))
            ),
    })
    ApiResponse<SendCoinSuccessDto> sendCoin(@PathVariable("crew_id") Long crewId,
                                                    @PathVariable("coin_count") Integer coin) throws InterruptedException;

    @Operation(
            summary = "크루 멤버 조회",
            description = "크루의 멤버를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "크루 멤버 조회 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"memberList\": [\n" +
                                    "      {\n" +
                                    "        \"nickname\": \"달리기 왕자\",\n" +
                                    "        \"userId\": 20,\n" +
                                    "        \"userProfileImage\": \"crew-profile-image.jpg\",\n" +
                                    "        \"exerciseTime\": 123123\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"nickname\": \"달리기 공주\",\n" +
                                    "        \"userId\": 21,\n" +
                                    "        \"userProfileImage\": \"crew-profile-image.jpg\",\n" +
                                    "        \"exerciseTime\": 123123\n" +
                                    "      }\n" +
                                    "    ]\n" +
                                    "  }\n" +
                                    "}"
                            )
                    )
            )
    })
    ApiResponse<CrewMembersResponseDto> getCrewMembers(@PathVariable("crew_id") Long crewId);

    @Operation(
            summary = "크루 점수 조회",
            description = "크루 아이디를 통해 크루 점수를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "크루 점수 조회 완료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"basicScore\": 1200,\n" +
                                    "    \"activityScore\": 800\n" +
                                    "  }\n" +
                                    "}"
                            ))
            ),
    })
    ApiResponse<CrewScoreResponseDto> getCrewScore(@PathVariable("crew_id") Long crewId);

    @Operation(
            summary = "크루 내 멤버 랭킹 조회",
            description = "크루의 현재 랭킹을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "크루 내 멤버 랭킹 조회 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"memberList\": [\n" +
                                    "      {\n" +
                                    "        \"nickname\": \"달리기 왕자\",\n" +
                                    "        \"userId\": 20,\n" +
                                    "        \"userProfileImage\": \"crew-profile-image.jpg\",\n" +
                                    "        \"exerciseTime\": 123123\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"nickname\": \"달리기 공주\",\n" +
                                    "        \"userId\": 21,\n" +
                                    "        \"userProfileImage\": \"crew-profile-image.jpg\",\n" +
                                    "        \"exerciseTime\": 123121\n" +
                                    "      }\n" +
                                    "    ]\n" +
                                    "  }\n" +
                                    "}"
                            )
                    )
            )
    })
    ApiResponse<CrewMembersResponseDto> getCrewRanking(@PathVariable("crew_id") Long crewId);

    @Operation(
            summary = "크루 배틀 생성",
            description = "배틀 시작 요청이 들어온 크루의 배틀을 생성합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "크루 배틀 생성 완료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"battleId\": 1,\n" +
                                    "    \"myTeamName\": \"달리자크루\",\n" +
                                    "    \"myTeamScore\": 0,\n" +
                                    "    \"opponentTeamName\": \"크크크루\",\n" +
                                    "    \"opponentTeamScore\": 0.0,\n" +
                                    "    \"exerciseName\": \"러닝\",\n" +
                                    "    \"dDay\": 2\n" +
                                    "  }\n" +
                                    "}"
                            ))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "크루 배틀 생성 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 409,\n" +
                                    "  \"message\": \"이미 진행중인 배틀이 존재합니다.\",\n" +
                                    "  \"data\": {}\n" +
                                    "}"
                            ))
            ),
    })
    ApiResponse<BattleMatchResponseDto> startCrewBattle(@PathVariable("crew_id") Long crewId);

    @Operation(
            summary = "크루 배틀 현황 조회",
            description = "크루 아이디를 통해 크루 배틀 현황을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "크루 배틀 현황 조회 완료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"myTeamName\": \"달리자크루\",\n" +
                                    "    \"myTeamScore\": 400.0,\n" +
                                    "    \"opponentTeamName\": \"크크크루\",\n" +
                                    "    \"opponentTeamScore\": 500.0,\n" +
                                    "    \"exerciseName\": \"러닝\",\n" +
                                    "    \"battleStatus\": \"STARTED\",\n" +
                                    "    \"dDay\": 2\n" +
                                    "  }\n" +
                                    "}"
                            ))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "배틀 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 204,\n" +
                                    "  \"message\": \"현재 진행 중인 배틀이 없습니다.\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"myTeamName\": \"No Battle\",\n" +
                                    "    \"myTeamScore\": 0.0,\n" +
                                    "    \"opponentTeamName\": \"No Opponent\",\n" +
                                    "    \"opponentTeamScore\": 0.0,\n" +
                                    "    \"exerciseName\": \"N/A\",\n" +
                                    "    \"battleStatus\": \"NONE\",\n" +
                                    "    \"dDay\": 0\n" +
                                    "  }\n" +
                                    "}"))
            )
    })
    ApiResponse<BattleMatchResponseDto> getBattleStatus(@PathVariable("crew_id") Long crewId);


    @Operation(
            summary = "크루 배틀 랜덤 매칭 동의, 취소",
            description = "크루 배틀 랜덤 매칭에 동의, 취소 합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "크루 배틀 랜덤 매칭 동의 완료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Success",
                                      "data": {
                                        "message": "크루 배틀 랜덤 매칭 동의에 성공하였습니다."
                                      }
                                    }
                                    """
                            ))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "크루 배틀 랜덤 매칭 동의 취소 완료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "message": "Success",
                                      "data": {
                                        "message": "크루 배틀 랜덤 매칭 동의를 취소하였습니다."
                                      }
                                    }
                                    """
                            ))
            ),
            
    })
    ApiResponse<BattleReadyStatusResponse> readyCrewBattle(@PathVariable("crew_id") Long crewId);

    @Operation(
            summary = "운동별 크루 랭킹 조회",
            description = "해당 운동 크루의 현재 랭킹을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "크루 랭킹 조회 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"crewList\": [\n" +
                                    "      {\n" +
                                    "        \"crewId\": 2,\n" +
                                    "        \"crewName\": \"달리자\",\n" +
                                    "        \"exerciseName\": \"러닝\",\n" +
                                    "        \"crewProfileImage\": \"crew-profile-image.jpg\",\n" +
                                    "        \"basicScore\": 0.0, \n"+
                                    "        \"activityScore\": 0.0 \n"+
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"crewId\": 3,\n" +
                                    "        \"crewName\": \"달리자\",\n" +
                                    "        \"exerciseName\": \"러닝\",\n" +
                                    "        \"crewProfileImage\": \"crew-profile-image.jpg\",\n" +
                                    "        \"basicScore\": 0.0, \n"+
                                    "        \"activityScore\": 0.0 \n"+
                                    "      }\n" +
                                    "    ]\n" +
                                    "  }\n" +
                                    "}"
                            )
                    )
            ),
    })
    ApiResponse<CrewListResponseDto> getCrewRankingByExercise(@PathVariable("exercise_id") Long exerciseId);


    @Operation(
            summary = "특정 크루의 크루원들의 오늘 운동시간과 캐릭터 조회",
            description = "특정 크루의 모든 크루원들의 오늘 운동 시간과 케릭터를 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "운동 시간 및 캐릭터 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "status": 200,
                      "message": "Success",
                      "data": {
                        "exerciseTimeList": [
                          {
                            "userId": 1,
                            "nickname": "JiEung",
                            "exerciseTime": 0,
                            "characterImageUrl": "image.png"
                          },
                          {
                            "userId": 2,
                            "nickname": "JiEung2",
                            "exerciseTime": 0,
                            "characterImageUrl": "image.png"
                          }
                        ]
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "크루 ID가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "message": "",
                      "data": null
                    }
                    """
                            )
                    )
            )
    })
    ApiResponse<CrewMemberDailyDetailListDto> getCrewMemberDailyDetailList(@PathVariable("crew_id") Long crewId);

    @Operation(
            summary = "크루 세팅 상태 조회",
            description = "내가 리더인 크루의 세팅 상태를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "크루 세팅 상태 조회 완료",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                      "status": 200,
                      "message": "",
                      "data": {
                        "battleStatus": true
                      }
                    }
                    """
                            )
                    )
            )
    })
    ApiResponse<CrewSettingResponseDto> getCrewSetting(@PathVariable("crew_id") Long crewId);


    @Operation(
            summary = "크루 이름 중복 확인",
            description = "입력된 이름이 이미 존재하는지 확인하고, 사용 가능한 닉네임인지 검사합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용 가능한 이름",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"message\": \"사용 가능한 이름입니다.\",\n" +
                                    "  \"data\": {\n" +
                                    "  }\n" +
                                    "}"
                            ))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 이름",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 409,\n" +
                                    "  \"message\": \"이미 사용중인 이름입니다.\",\n" +
                                    "  \"data\": {}\n" +
                                    "}"
                            ))
            ),
    })
    ApiResponse<ValidateNameSuccessDto> validateCrewName(@PathVariable("crew_name") String crewName);

}