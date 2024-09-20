// src/mocks/browser.ts
import { setupWorker } from 'msw/browser';
import { handlers } from './handlers';

// 서비스 워커 설정
export const worker = setupWorker(...handlers);
