import logging
from pathlib import Path

LOGGER_NAME = "employee_app"
PROJECT_ROOT = Path(__file__).resolve().parents[2]
LOG_FOLDER = PROJECT_ROOT / "logs"
LOG_FORMAT = "%(asctime)s - %(name)s - %(levelname)s - %(message)s"


def setup_logger():
    LOG_FOLDER.mkdir(parents=True, exist_ok=True)

    logging.basicConfig(
        filename=LOG_FOLDER / "employee_app.log",
        level=logging.INFO,
        format=LOG_FORMAT,
        encoding="utf-8",
        force=True
    )

def get_logger():
    return logging.getLogger(LOGGER_NAME)