import { DateTime } from 'luxon';

const ZONE = 'Europe/Rome';

/**
 * Convert a date in ISO format to a date in the UTC timezone.
 *
 * @param isoDate the date to convert in ISO format
 */
export function convertToUTC(isoDate: string): string {
    const convertedDateTime = DateTime.fromISO(isoDate, { zone: ZONE });
    return convertedDateTime.toUTC().toISO() as string;
}

/**
 * Convert a date in UTC format to a date in the ISO format.
 *
 * @param utcDate the date to convert in UTC format
 */
export function convertToISO(utcDate: string): string {
    const convertedDateTime = DateTime.fromISO(utcDate, { zone: 'UTC' }).setZone(ZONE);
    return convertedDateTime.toISO() as string;
}
