# 캠퍼스 기자재 대여 앱 — final

캠퍼스 기자재를 조회하고 대여 신청 내용을 입력·확인하는 Android 학습용 앱입니다. `final`은 로그인과 Room DB를 도입하기 전, 기본 화면 흐름을 구성한 개발 기준 버전입니다.

## 브랜치별 개발 단계

| 브랜치 | 개발 범위 |
| --- | --- |
| `final` | 홈 → 기자재 목록 → 상세 → 대여 신청 → 완료 화면. 샘플 기자재 조회와 Bundle을 통한 신청 정보 전달 |
| `week6-room` | 사용자 ID 로그인·세션 추가, Room을 통한 대여 신청 저장, 사용자·기자재 Entity/DAO 및 AppDatabase 구성 |
| `week7-room` | 회원가입 화면·내비게이션, `users` 저장·조회 로그인, 기본 회원 초기 저장, 중복 가입 방지 및 DB 마이그레이션 추가 |

## 이 버전의 주요 기능과 코드

| 영역 | 주요 코드 | 구현 내용 |
| --- | --- | --- |
| 화면 구성 | `MainActivity.kt`, `activity_main.xml` | ViewBinding과 내비게이션 호스트를 사용하고 시스템 바 영역의 여백 처리 |
| 홈 | `ui/home/HomeFragment.kt` | 기자재 목록 화면으로 이동 |
| 기자재 데이터 | `model/Equipment.kt`, `data/EquipmentRepository.kt` | 기자재 정보·대여 상태 모델, 8개 샘플 기자재 목록과 ID 조회 함수 |
| 목록·상세 | `adapter/EquipmentAdapter.kt`, `ui/list/EquipmentListFragment.kt`, `ui/detail/EquipmentDetailFragment.kt` | 기자재 목록 및 상세 정보 표시, 대여 가능 여부에 따른 신청 버튼 처리 |
| 대여 신청 | `ui/rental/RentalFragment.kt` | 신청자 이름·학번·대여 목적의 빈 입력 검사, 기자재별 최대 대여 기간 내 선택, 신청 정보 전달 |
| 신청 완료 | `ui/complete/CompleteFragment.kt` | 전달받은 기자재·신청자·대여 기간·목적 표시 및 목록·홈 이동 |
| 화면 간 연결 | `res/navigation/nav_graph.xml`, `util/NavKeys.kt` | 홈을 시작 화면으로 설정하고 Bundle 키를 이용해 화면 간 데이터 전달 |

## 주요 개발 이력

| 커밋 | 진행 내용 |
| --- | --- |
| `e0c7591` | 기본 프로젝트 구성 (`complete project commit`) |
| `7853495` | 후속 프로젝트 코드 반영 (`complete project commit`) |
| `5802491` | 화면·코드 보완 (`코드 추가 수정 seoyk 2026.09.29`). 후속 Room 개발의 기준 스냅샷 |

이후 `week6-room`에서 로그인·세션과 로컬 DB 저장을 확장하고, `week7-room`에서 회원가입과 DB 기반 로그인을 연결했습니다. 각 브랜치의 README에서 해당 단계의 변경 사항을 확인할 수 있습니다.

## 데이터 처리 범위

기자재는 `EquipmentRepository`의 메모리 내 샘플 데이터를 사용합니다. 대여 신청은 화면 간에 전달되어 완료 화면에 표시되며 DB에 영구 저장하지 않습니다. 로그인·회원가입·사용자 세션·Room Entity/DAO는 후속 브랜치에서 추가됩니다.

이번 README 추가는 개발 단계 문서화 작업이며 앱 코드는 변경하지 않았습니다. 이 작업에서는 빌드와 테스트를 재실행하지 않았습니다.
